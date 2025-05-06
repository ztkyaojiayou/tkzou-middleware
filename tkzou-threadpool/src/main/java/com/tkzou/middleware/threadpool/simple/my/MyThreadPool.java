package com.tkzou.middleware.threadpool.simple.my;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author zoutongkun
 **/
public class MyThreadPool {

    private final int corePoolSize;
    private final int maxSize;
    private final int timeout;
    private final TimeUnit timeUnit;
    public final BlockingQueue<Runnable> blockingQueue;
    private final RejectHandle rejectHandle;

    public MyThreadPool(int corePoolSize, int maxSize, int timeout, TimeUnit timeUnit,
                        BlockingQueue<Runnable> blockingQueue, RejectHandle rejectHandle) {
        this.corePoolSize = corePoolSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHandle = rejectHandle;
    }

    /**
     * 核心线程
     */
    List<Thread> coreList = new ArrayList<>();
    /**
     * 辅助线程
     */
    List<Thread> supportList = new ArrayList<>();

    /**
     * 提交任务
     *
     * @param command
     */
    public void execute(Runnable command) {
        //没到核心线程数时，直接创建，每一个核心线程都在循环处理队列这的任务哦！！！
        if (coreList.size() < corePoolSize) {
            Thread thread = new CoreWorker();
            coreList.add(thread);
            thread.start();
        }
        //若达到了，则
        if (blockingQueue.offer(command)) {
            return;
        }
        //若阻塞队列也满了，则开始创建辅助线程
        if (coreList.size() + supportList.size() < maxSize) {
            Thread thread = new SupportWorker();
            supportList.add(thread);
            thread.start();
        }

        //此时继续往阻塞队列中添加任务，若还是满了，则直接执行拒绝策略！
        if (!blockingQueue.offer(command)) {
            rejectHandle.reject(command, this);
        }
    }

    /**
     * 核心线程worker
     */
    class CoreWorker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 辅助线程worker
     */
    class SupportWorker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit);
                    if (command == null) {
                        break;
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + "线程结束了！");
        }
    }
}
