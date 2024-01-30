package com.tkzou.middleware.threadpool.dtp.v2;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;

/**
 * 动态线程池实现
 * 基于jdk线程池
 *
 * @author zoutongkun
 */
public class DtpExecutor extends ThreadPoolExecutor {
    private String threadPoolId;

    /**
     * 构造器
     *
     * @param threadPoolId
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    public DtpExecutor(String threadPoolId, int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, wrapProxyRejectedHandler(threadPoolId, handler));
        this.threadPoolId = threadPoolId;
    }

    /**
     * 构造器
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
     */
    public DtpExecutor(String threadPoolId, Long maxRejectNum, int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, wrapProxyRejectedHandler(threadPoolId, handler, maxRejectNum));
        this.threadPoolId = threadPoolId;
    }

    /**
     * 增强拒绝策略
     *
     * @param threadPoolId
     * @param rejectedExecutionHandler
     * @return
     */
    public static RejectedExecutionHandler wrapProxyRejectedHandler(String threadPoolId, RejectedExecutionHandler rejectedExecutionHandler) {
        return (RejectedExecutionHandler) Proxy.newProxyInstance(rejectedExecutionHandler.getClass().getClassLoader(), new Class[]{rejectedExecutionHandler.getClass()}, new RejectedInvocationHandler(rejectedExecutionHandler, threadPoolId));
    }

    /**
     * 增强拒绝策略
     *
     * @param threadPoolId
     * @param rejectedExecutionHandler
     * @return
     */
    public static RejectedExecutionHandler wrapProxyRejectedHandler(String threadPoolId, RejectedExecutionHandler rejectedExecutionHandler, Long maxRejectNum) {
        //使用jdk动态代理生成代理对象
        return (RejectedExecutionHandler) Proxy.newProxyInstance(rejectedExecutionHandler.getClass().getClassLoader(), new Class[]{rejectedExecutionHandler.getClass()}, new RejectedInvocationHandler(rejectedExecutionHandler, threadPoolId, maxRejectNum));
    }

    /**
     * 执行无返回值的任务
     *
     * @param runnable
     */
    @Override
    public void execute(@NotNull Runnable runnable) {
        super.execute(runnable);
    }

    /**
     * 执行带返回值的任务
     *
     * @param task
     * @param <T>
     * @return
     */
    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        return super.submit(task);
    }
}
