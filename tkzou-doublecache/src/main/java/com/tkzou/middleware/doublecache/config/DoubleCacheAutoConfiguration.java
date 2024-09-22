package com.tkzou.middleware.doublecache.config;

import com.tkzou.middleware.doublecache.common.NamedThreadFactory;
import com.tkzou.middleware.doublecache.core.cache.DoubleCacheService;
import com.tkzou.middleware.doublecache.core.cache.FirstLevelCacheService;
import com.tkzou.middleware.doublecache.core.cache.SecondLevelCacheService;
import com.tkzou.middleware.doublecache.core.listener.CacheUpdateMessageListener;
import com.tkzou.middleware.doublecache.core.notify.NotifyByRedisImpl;
import com.tkzou.middleware.doublecache.core.notify.NotifyService;
import com.tkzou.middleware.doublecache.utils.SpringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.codec.LZ4Codec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 两级缓存自动配置类
 *
 * @author zoutongkun
 */
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
@Order(10)
@ComponentScan(basePackages = "com.tkzou.middleware.doublecache")
public class DoubleCacheAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DoubleCacheAutoConfiguration.class);
    public static final String REDIS_EXECUTOR = "redisExecutor";
    public static final String THREAD_NAME_PREFIX = "Redisson-Pool";

    /**
     * 缓存参数配置
     */
    @Autowired
    private DoubleCacheConfig doubleCacheConfig;

    /**
     * 线程池等待结束时间
     */
    private static final int awaitTerminationSeconds = 60;

    /**
     * 线程池配置
     *
     * @return
     */
    @Bean
    public ExecutorService redisExecutor() {
        return new ThreadPoolExecutor(
                doubleCacheConfig.getExecutorCoreSize(),
                doubleCacheConfig.getExecutorMaxSize(),
                doubleCacheConfig.getExecutorAliveTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(doubleCacheConfig.getExecutorQueueCapacity()),
                new NamedThreadFactory(THREAD_NAME_PREFIX));
    }

    /**
     * 增加关闭钩子处理， 实现Redisson线程池优雅关闭
     */
    @PostConstruct
    public void addShutdown() {
        Object redisExecutorObj = SpringUtils.getBean(REDIS_EXECUTOR);
        if (null != redisExecutorObj) {
            ExecutorService redisExecutor = (ExecutorService) redisExecutorObj;
            // 关闭钩子处理
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                redisExecutor.shutdown();
                try {
                    if (!redisExecutor.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS)) {
                        logger.info("Redisson Pool Executor did not terminate in the specified time.");
                        List<Runnable> droppedTasks = redisExecutor.shutdownNow();
                        logger.info("Redisson Pool Executor was abruptly shutdown. " + droppedTasks.size() + " tasks " +
                                "will not be executed."); //optional **
                    }
                } catch (InterruptedException e) {
                    logger.error("Redisson Pool Executor shutdown Error: " + e.getMessage(), e);
                }
            }));
        }
    }

    @Bean
    public RedissonClient redissonClient(ExecutorService redisExecutor) {
        Config config = new Config();
        RedissonClient redisson;
        if (null != doubleCacheConfig.getHost()) {
            // 单机连接方式
            SingleServerConfig serverConfig =
                    config.useSingleServer().setAddress("redis://" + doubleCacheConfig.getHost() + ":" + doubleCacheConfig.getPort());
            serverConfig.setDatabase(doubleCacheConfig.getDatabase());
            serverConfig.setPassword(doubleCacheConfig.getPassword());
            serverConfig.setConnectionMinimumIdleSize(doubleCacheConfig.getMinIdleSize());
            serverConfig.setConnectionPoolSize(doubleCacheConfig.getPoolMaxSize());
            serverConfig.setConnectTimeout(doubleCacheConfig.getTimeout());
            serverConfig.setTimeout(doubleCacheConfig.getTimeout());
            redisson = Redisson.create(config);
        } else {
            if (null == doubleCacheConfig.getClusterNodes()) {
                throw new RuntimeException("You need to config the clusterNodes property!");
            }
            // 集群连接方式
            ClusterServersConfig serversConfig = config.useClusterServers();
            serversConfig.setConnectTimeout(doubleCacheConfig.getTimeout());
            serversConfig.setTimeout(doubleCacheConfig.getTimeout());
            serversConfig.setMasterConnectionPoolSize(doubleCacheConfig.getPoolMaxSize());
            serversConfig.setSlaveConnectionPoolSize(doubleCacheConfig.getPoolMaxSize());
            serversConfig.setMasterConnectionMinimumIdleSize(doubleCacheConfig.getMinIdleSize());
            serversConfig.setSlaveConnectionMinimumIdleSize(doubleCacheConfig.getMinIdleSize());

            Arrays.stream(doubleCacheConfig.getClusterNodes().split(",")).forEach(host -> serversConfig.addNodeAddress(
                    "redis://" + host.trim()));
            serversConfig.setPassword(doubleCacheConfig.getPassword());
            redisson = Redisson.create(config);
        }
        redisson.getConfig().setExecutor(redisExecutor);
        if (doubleCacheConfig.isUseCompression()) {
            // 开启压缩, 采用LZ4压缩
            redisson.getConfig().setCodec(new LZ4Codec());
        } else {
            // 高速序列化编码
            redisson.getConfig().setCodec(new FstCodec());

        }
        return redisson;
    }


    /**
     * Redis缓存更新消息发送接口
     *
     * @param doubleCacheConfig
     * @param redissonClient
     * @return
     */
    @Bean
    public NotifyService notifyService(DoubleCacheConfig doubleCacheConfig,
                                       RedissonClient redissonClient) {
        return new NotifyByRedisImpl(doubleCacheConfig, redissonClient);
    }

    /**
     * 缓存服务实现接口
     *
     * @return
     */
    @Bean
    public DoubleCacheService cacheService(RedissonClient redissonClient,
                                           NotifyService notifyService,
                                           ExecutorService redisExecutor) {
        DoubleCacheService doubleCacheService;
        // 判断是否开启两级缓存，默认只开启redis缓存
        if (doubleCacheConfig.isEnableSecondCache()) {
            DoubleCacheService secondDoubleCacheService = new FirstLevelCacheService(redissonClient, redisExecutor, doubleCacheConfig);
            doubleCacheService = new SecondLevelCacheService(secondDoubleCacheService, notifyService, doubleCacheConfig);
        } else {
            doubleCacheService = new FirstLevelCacheService(redissonClient, redisExecutor, doubleCacheConfig);
        }
        return doubleCacheService;
    }


    /**
     * 设置redis消息监听器
     * 只有在开启了两级缓存时才注入！！！
     *
     * @param redissonClient
     * @param caffeineDoubleCacheService
     * @return
     */
    @ConditionalOnProperty(
            value = "app.cache.enableSecondCache",
            havingValue = "true")
    @Bean
    public RTopic subscribe(RedissonClient redissonClient, DoubleCacheService caffeineDoubleCacheService) {
        RTopic rTopic = redissonClient.getTopic(doubleCacheConfig.getTopic());
        CacheUpdateMessageListener messageListener =
                new CacheUpdateMessageListener((SecondLevelCacheService) caffeineDoubleCacheService);
        rTopic.addListener(messageListener);
        return rTopic;
    }

}
