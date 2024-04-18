package com.tkzou.middleware.configcenter.client.core;

import com.tkzou.middleware.configcenter.client.client.domain.ConfigFile;
import com.tkzou.middleware.configcenter.client.client.listener.ConfigFileChangedListener;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 配置服务核心类
 * <p>
 * 需要加载至ioc容器中，对于框架，一般不会单独在各个类上加@Component注解的，
 * 一般为了统一，会使用专门的配置类来统一添加！
 * 比如该项目中就是使用ConfigCenterAutoConfiguration类来统一管理bean！！！
 * 同时会以构造器的方式在生成当前bean时将configClient也实例化（就是使用最原始的new的方式!）！
 * 也即相当于在构造器中做业务操作！！！这个思路在框架中常用，务必掌握！！！
 *
 * @author zoutongkun
 * @date 2022/9/30 10:37
 */
public class ConfigService {
    /**
     * 单线程的线程池，用于当时拉取配置中心的配置信息
     */
    private static final ScheduledExecutorService THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1,
            new ThreadFactory() {
                private final AtomicLong atomicLong = new AtomicLong();

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "ConfigService-" + atomicLong.getAndIncrement());
                    //作为守护线程在后台运行！
                    thread.setDaemon(true);
                    return thread;
                }
            });

    /**
     * 配置文件id和对应监听器映射
     * 在ioc容器初始化完成后，由ConfigContextRefresher
     * 监听ApplicationReadyEvent事件来初始化该map
     */
    private final Map<String, ConfigFileChangedListener> configFileChangeListenerMap = new ConcurrentHashMap<>();

    /**
     * 文件id和文件的映射
     * 属于本地缓存，即存储当前配置文件的信息，当配置文件发生变化时会及时更新！！！
     */
    private final Map<String, ConfigFile> configFileCache = new ConcurrentHashMap<>();

    /**
     * 获取所有配置文件信息
     *
     * @return
     */
    public List<ConfigFile> getAllConfigFiles() {
        List<ConfigFile> configFiles = new ArrayList<>();
        for (Map.Entry<String, ConfigFile> curEntry : configFileCache.entrySet()) {
            ConfigFile configFile = curEntry.getValue();
            configFiles.add(configFile);
        }
        return configFiles;
    }

    /**
     * 跟服务端交互的client
     */
    private final ConfigRpcClient configRpcClient;

    /**
     * 构造器，此时配置中心的配置文件已经读取过一次了，只需要刷新即可！
     *
     * @param serverAddr
     */
    public ConfigService(String serverAddr) {
        //1.将configClient也实例化，就是使用的最原始的new的方式！！！
        this.configRpcClient = new ConfigRpcClient(serverAddr);
        //2.同时启用一个定时任务，完成新配置的拉取，其实属于业务操作了！！！
        // 这里做两件事：
        // 1.将最新配置拉取到本地保存
        // 2.发布配置更新事件，springboot通过监听该事件来动态刷新ioc容器
        startCheckAndUpdateConfigJob();
    }

    /**
     * 给配置文件添加一个监听器
     * 注意：是对每一个配置文件都要设置一个监听器，只是我们这里的入参是一个
     *
     * @param fileId                   目标文件/也即我们项目中的配置文件
     * @param configFileChangeListener
     */
    public void addListener(String fileId, ConfigFileChangedListener configFileChangeListener) {
        configFileChangeListenerMap.put(fileId, configFileChangeListener);
    }

    /**
     * 根据配置文件id获取对应的配置文件信息，属于最新的数据
     */
    public ConfigFile getConfig(String fileId) {
        return configRpcClient.getConfig(fileId);
    }

    /**
     * 检查并更新配置文件
     */
    private void startCheckAndUpdateConfigJob() {
        //每隔5s中从配置中心拉取（pull）文件，判断文件是不是有更新，如果有更新就回调对应的监听器
        THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(this::notifyChangedConfigFile, 1, 5, TimeUnit.SECONDS);
    }

    private void notifyChangedConfigFile() {
        //遍历每一个已经从配置中心获取到的配置文件，检查并更新！
        for (Map.Entry<String, ConfigFileChangedListener> entry : configFileChangeListenerMap.entrySet()) {
            String fileId = entry.getKey();
            //1.从服务端拉取的最新配置文件
            //就是走http请求
            ConfigFile newConfigFile = configRpcClient.getConfig(fileId);
            // 2.校验文件是否有更新，若未更新，则跳过，否则发送事件更新通知！
            // 本地缓存的配置文件，即旧的配置文件
            ConfigFile oldConfigFile = configFileCache.get(fileId);
            if (!ObjectUtils.isEmpty(oldConfigFile) && !ObjectUtils.isEmpty(newConfigFile)) {
                if (oldConfigFile.getLastUpdateTimestamp() < newConfigFile.getLastUpdateTimestamp()) {
                    //当发现缓存的数据更新的时间小于最新查询的文件的更新时间，说明配置文件有更新，然后回调对应的监听器
                    //目的：通知spring-cloud进行动态刷新对应的bean属性（具体而言，其实是删除旧bean，再重新生成新bean，但对外是通过一个代理bean来实现)！！！
                    entry.getValue().onFileChanged(newConfigFile);
                }
            }
            //3.同时更新配置文件至本地缓存
            configFileCache.put(fileId, newConfigFile);
        }
    }

}
