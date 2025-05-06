package com.tkzou.middleware.threadpool.simple.my;

/**
 * @author zoutongkun
 **/
public interface RejectHandle {
    /**
     * 拒绝策略
     *
     * @param rejectCommand
     * @param threadPool
     */
    void reject(Runnable rejectCommand, MyThreadPool threadPool);
}
