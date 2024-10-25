package com.tkzou.middleware.threadpool.dtp.v1.common.pool;

import com.tkzou.middleware.threadpool.dtp.v1.common.support.ExecutorDecorator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;

/**
 * 动态线程池
 *
 * @author zoutongkun
 * @Date 2023/5/19 22:10
 */
@Slf4j
public class DtpExecutor extends ThreadPoolExecutor implements ExecutorDecorator<ThreadPoolExecutor> {
    public DtpExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public DtpExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public DtpExecutor(String threadPoolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, wrapProxyRejectedHandler(threadPoolName, handler));
    }

    public DtpExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 任务执行前
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    /**
     * 任务执行后
     *
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    public long getTaskNum() {
        return getTaskCount();
    }

    @Override
    public int getQueueSize() {
        return getQueue().size();
    }

    @Override
    public int getQueueRemainingCapacity() {
        return getQueue().remainingCapacity();
    }

    @Override
    public ThreadPoolExecutor getOrigins() {
        return this;
    }

    @Override
    public void destroy() throws Exception {
        log.info("线程池已被关闭");
        this.shutdown();
    }

    /**
     * 增强拒绝策略
     * 使用了代理模式
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

}
