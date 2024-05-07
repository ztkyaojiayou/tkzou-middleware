package com.tkzou.middleware.threadpool.simple.v3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author zoutongkun
 */
public class ThreadPool {
    /**
     * 任务队列
     */
    private final BlockingQueue<Runnable> taskQueue;
    /**
     * 线程池状态
     */
    private volatile boolean isStopped;
    /**
     * 执行任务的worker
     */
    private final List<Worker> workers = new ArrayList<>();

    /**
     * 构造器
     *
     * @param numThreads
     * @param maxTasks
     */
    public ThreadPool(int numThreads, int maxTasks) {
        this.taskQueue = new ArrayBlockingQueue(maxTasks);
        for (int i = 0; i < numThreads; i++) {
            workers.add(new Worker(this.taskQueue));
        }
        int i = 1;
        for (Worker worker : workers) {
            new Thread(worker, "ThreadPool-" + i + "-thread").start();
            i++;
        }
    }

    /**
     * 向线程池提交任务
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void execute(Runnable runnable) throws InterruptedException {
        if (isStopped) {
            // 如果线程池已经停下来了，就不在向任务队列当中提交任务了
            System.err.println("thread pool has been stopped, so quit submitting task");
            return;
        }
        taskQueue.put(runnable);
    }

    /**
     * 强制关闭线程池
     */
    public synchronized void stop() {
        isStopped = true;
        for (Worker worker : workers) {
            worker.stopWorker();
        }
    }

    /**
     * 优雅关闭线程池
     */
    public synchronized void shutDown() {
        // 先表示关闭线程池 线程就不能再向线程池提交任务
        isStopped = true;
        // 先等待所有的任务执行完成再关闭线程池
        waitForAllTasks();
        stop();
    }

    /**
     * 等待队列中的任务执行完毕
     */
    private void waitForAllTasks() {
        // 当线程池当中还有任务的时候 就不退出循环
        while (taskQueue.size() > 0) {
            Thread.yield();
        }
    }
}