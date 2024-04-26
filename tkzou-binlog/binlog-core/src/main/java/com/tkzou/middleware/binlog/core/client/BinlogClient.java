package com.tkzou.middleware.binlog.core.client;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.tkzou.middleware.binlog.core.BinlogPositionHandler;
import com.tkzou.middleware.binlog.core.IBinlogClient;
import com.tkzou.middleware.binlog.core.IBinlogEventHandler;
import com.tkzou.middleware.binlog.core.common.enums.BinlogClientMode;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.core.config.RedisConfig;
import com.tkzou.middleware.binlog.core.dispatcher.BinlogEventDispatcher;
import com.tkzou.middleware.binlog.core.exception.BinlogException;
import com.tkzou.middleware.binlog.core.handler.BinlogEventHandlerInvoker;
import com.tkzou.middleware.binlog.core.persistence.BinlogPosition;
import com.tkzou.middleware.binlog.core.persistence.RedisBinlogPositionHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Binlog 客户端
 * 整体采用订阅发布/监听者模式
 * 需要读取配置
 *
 * @author zoutongkun
 */
@Slf4j
public class BinlogClient implements IBinlogClient {

    private final BinlogClientConfig clientConfig;

    private BinaryLogClient client;

    private BinlogPositionHandler positionHandler;

    private RedissonClient redissonClient;
    /**
     * 事件处理器map
     * 也可以理解为事件处理器的注册中心
     */
    private final Map<String, BinlogEventHandlerInvoker> eventHandlerList = new HashMap<>();

    private final ExecutorService executor;

    private BinlogClient(BinlogClientConfig clientConfig) {
        this.createPositionHandler(clientConfig);
        this.createRedissonClient(clientConfig);
        this.clientConfig = clientConfig;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * 创建一个client
     *
     * @param clientConfig
     * @return
     */
    public static BinlogClient create(BinlogClientConfig clientConfig) {
        return new BinlogClient(clientConfig);
    }

    /**
     * 注册事件处理器
     *
     * @param handlerKey   具名 Key
     * @param eventHandler 事件处理器
     */
    @Override
    public void registerEventHandler(String handlerKey, IBinlogEventHandler eventHandler) {
        BinlogEventHandlerInvoker eventHandlerDetails = new BinlogEventHandlerInvoker();
        eventHandlerDetails.setClientConfig(clientConfig);
        eventHandlerDetails.setEventHandler(eventHandler);
        this.eventHandlerList.put(handlerKey, eventHandlerDetails);
    }

    @Override
    public void registerEventHandler(IBinlogEventHandler eventHandler) {
        //此时key随机
        this.registerEventHandler(UUID.randomUUID().toString(), eventHandler);
    }

    @Override
    public void unregisterEventHandler(String handlerKey) {
        if (eventHandlerList.containsKey(handlerKey)) {
            eventHandlerList.remove(handlerKey);
        }
    }

    @Override
    public void connect() {
        BinlogClientMode clientMode = clientConfig.getMode();
        //集群部署时
        if (clientMode == BinlogClientMode.cluster) {
            ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
            //启动一个定时任务去连接，每秒执行一次，防止宕机！
            scheduledExecutor.scheduleWithFixedDelay(this::runWithCluster, 0, 1000, TimeUnit.MILLISECONDS);
        } else {
            //单机部署时
            //使用线程池异步执行，防止阻塞项目主进程，
            //这是中间件接入时一贯的处理方式，如nacos！！！
            executor.submit(this::runWithStandalone);
        }
    }

    @Override
    public void disconnect() {
        if (client != null) {
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 项目单机启动时连接到mysql
     */
    public void runWithStandalone() {
        try {
            log.info("启动 Binlog 客户端 ({}) - 连接 {}:{} 服务", clientConfig.getServerId(), clientConfig.getHost(), clientConfig.getPort());
            client = new BinaryLogClient(clientConfig.getHost(), clientConfig.getPort(), clientConfig.getUsername(), clientConfig.getPassword());
            //注册核心的监听器--BinlogEventDispatcher
            client.registerEventListener(new BinlogEventDispatcher(this.clientConfig, positionHandler, this.eventHandlerList));
            client.setKeepAlive(clientConfig.getKeepAlive());
            client.setKeepAliveInterval(clientConfig.getKeepAliveInterval());
            client.setHeartbeatInterval(clientConfig.getHeartbeatInterval());
            client.setConnectTimeout(clientConfig.getConnectTimeout());
            client.setServerId(clientConfig.getServerId());
            //是否持久化
            if (clientConfig.getPersistence()) {
                //是否为首次启动
                if (!clientConfig.isInaugural()) {
                    if (positionHandler != null) {
                        //从上次处理结束的位置开始接着处理
                        BinlogPosition lastPosition = positionHandler.getLastPosition(clientConfig.getServerId());
                        if (lastPosition != null) {
                            client.setBinlogFilename(lastPosition.getFilename());
                            client.setBinlogPosition(lastPosition.getPosition());
                        }
                    }
                }
            }
            //连接到mysql，此时就开始实时监听mysql的binlog啦！！！
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 项目集群启动时连接到mysql
     */
    public void runWithCluster() {
        //加redis分布式锁，因为是集群部署，mysql只有一个
        //要注意的，指的是咱们自己的项目是集群部署，不是说是mysql集群部署！！！
        RLock lock = redissonClient.getLock(clientConfig.getKey());
        try {
            if (lock.tryLock()) {
                runWithStandalone();
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void createPositionHandler(BinlogClientConfig clientConfig) {
        if (clientConfig.getPersistence()) {
            if (clientConfig.getPositionHandler() == null) {
                if (clientConfig.getRedisConfig() == null) {
                    throw new BinlogException("Cluster mode or persistence enabled, missing Redis configuration");
                } else {
                    this.positionHandler = new RedisBinlogPositionHandler(clientConfig.getRedisConfig());
                }
            } else {
                this.positionHandler = clientConfig.getPositionHandler();
            }
        }
    }

    private void createRedissonClient(BinlogClientConfig clientConfig) {
        if (clientConfig.getMode() == BinlogClientMode.cluster) {
            RedisConfig redisConfig = clientConfig.getRedisConfig();
            if (redisConfig == null) {
                throw new BinlogException("Cluster mode or persistence enabled, missing Redis configuration");
            }
            Config config = new Config();
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());
            singleServerConfig.setPassword(redisConfig.getPassword());
            singleServerConfig.setDatabase(redisConfig.getDatabase());
            config.setLockWatchdogTimeout(10000L);
            this.redissonClient = Redisson.create(config);
        }
    }
}
