package com.tkzou.middleware.threadpool.dtp.v2;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.*;

/**
 * dtp线程池创建工厂
 *
 * @author zoutongkun
 */
@Slf4j
public class DtpExecutorFactory {
    private static final String CONFIG_CORE_SIZE = "coreSize";
    private static final String CONFIG_MAX_SIZE = "maxSize";
    /**
     * 用于监控线程池的定时线程池
     */
    private static final ScheduledExecutorService scheduleThreadPool = ThreadUtil.createScheduledExecutor(1);
    ;
    /**
     * 线程池注册/保存
     */
    private static final ConcurrentHashMap<String, DtpExecutor> threadPoolMap = new ConcurrentHashMap<>();

    //开启监控
    static {
        scheduleThreadPool.scheduleAtFixedRate(() -> {
            //监控指标
            threadPoolMap.forEach((threadPoolId, dtpExecutor) -> {
                log.info("{}-monitor: " +
                                "初始线程数: {}, 核心线程数: {}, 执行的任务数量: {}, " +
                                "已完成任务数量: {}, 任务总数: {}, 队列里缓存的任务数量: {}, 池中存在的最大线程数: {}, " +
                                "最大允许的线程数: {},  线程空闲时间: {}, 线程池是否关闭: {}, 线程池是否终止: {}",
                        threadPoolId,
                        dtpExecutor.getPoolSize(), dtpExecutor.getCorePoolSize(), dtpExecutor.getActiveCount(),
                        dtpExecutor.getCompletedTaskCount(), dtpExecutor.getTaskCount(), dtpExecutor.getQueue().size(), dtpExecutor.getLargestPoolSize(),
                        dtpExecutor.getMaximumPoolSize(), dtpExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS), dtpExecutor.isShutdown(), dtpExecutor.isTerminated());

                //todo 同样可以配置报警策略和触发报警！
                //  也可以接入influxDb进行可视化监控！！！
            });
            //每5秒监控一次！
        }, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * 创建dtp线程池
     *
     * @param threadPoolId
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     * @return
     */
    public static DtpExecutor createDtpExecutor(String threadPoolId, int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
        //创建线程池
        DtpExecutor dtpExecutor = new DtpExecutor(threadPoolId, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        //注册

        threadPoolMap.put(threadPoolId, dtpExecutor);
        return dtpExecutor;
    }

    /**
     * 创建dtp线程池
     *
     * @param threadPoolId
     * @param maxRejectNum
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     * @return
     */
    public static DtpExecutor createDtpExecutor(String threadPoolId, Long maxRejectNum, int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
        //创建线程池
        DtpExecutor dtpExecutor = new DtpExecutor(threadPoolId, maxRejectNum, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        //注册
        threadPoolMap.put(threadPoolId, dtpExecutor);
        return dtpExecutor;
    }

    /**
     * 刷新nacos配置信息
     *
     * @param configMap
     */
    public static void refreshDtpExecutorConfig(Map<String, Integer> configMap) {
        //这里是应用到所有线程池，也可以配置不同策略
        threadPoolMap.forEach((threadPoolId, dtpExecutor) -> {
            String coreSizeKey = threadPoolId + CONFIG_CORE_SIZE;
            String maxSizeKey = threadPoolId + CONFIG_MAX_SIZE;
            //获取配置值
            Integer newCoreSize = configMap.get(coreSizeKey);
            Integer newMaxSize = configMap.get(maxSizeKey);
            //设置配置值
            if (ObjectUtil.isNotEmpty(newCoreSize)) {
                dtpExecutor.setCorePoolSize(newCoreSize);
            }
            if (ObjectUtil.isNotEmpty(newMaxSize)) {
                dtpExecutor.setMaximumPoolSize(newMaxSize);
            }
            log.info("线程池{}参数修改成功！修改后的coreSize为{}，maxSize为{}", threadPoolId, dtpExecutor.getCorePoolSize(), dtpExecutor.getMaximumPoolSize());
        });

    }
}
