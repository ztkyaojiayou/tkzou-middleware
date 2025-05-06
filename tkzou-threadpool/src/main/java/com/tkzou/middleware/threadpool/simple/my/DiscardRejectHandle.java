package com.tkzou.middleware.threadpool.simple.my;

/**
 * @author zoutongkun
 **/
public class DiscardRejectHandle implements RejectHandle {
    @Override
    public void reject(Runnable rejectCommand, MyThreadPool threadPool) {
        threadPool.blockingQueue.poll();
        threadPool.execute(rejectCommand);
    }
}
