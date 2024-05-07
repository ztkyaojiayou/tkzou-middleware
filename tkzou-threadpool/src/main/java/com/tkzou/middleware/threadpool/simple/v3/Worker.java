package com.tkzou.middleware.threadpool.simple.v3;

import java.util.concurrent.BlockingQueue;

/**
 * @author zoutongkun
 */
public class Worker implements Runnable {
    /**
     * 表示正在执行任务的线程
     */
    private Thread thisThread;
    /**
     * 由线程池传递过来的任务队列
     */
    private BlockingQueue<Runnable> taskQueue;
    /**
     * 表示 worker 是否停止工作 需要使用 volatile 保证线程之间的可见性
     */
    private volatile boolean isStopped;

    /**
     * 构造器
     *
     * @param taskQueue
     */
    public Worker(BlockingQueue<Runnable> taskQueue) { // 这个构造方法是在线程池的实现当中会被调用
        this.taskQueue = taskQueue;
    }

    /**
     * 线程执行的函数
     */
    @Override
    public void run() {
        // 获取执行任务的线程
        thisThread = Thread.currentThread();
        // 当线程没有停止的时候就不断的去任务池当中取出任务
        while (!isStopped) {
            try {
                // 从任务池当中取出任务 当没有任务的时候线程会被这个方法阻塞
                Runnable task = taskQueue.take();
                task.run(); // 执行任务 任务就是一个 Runnable 对象
            } catch (InterruptedException e) {
                // do nothing
                // 这个地方很重要 你有没有思考过一个问题当任务池当中没有任务的时候 线程会被阻塞在 take 方法上
                // 如果我们后面没有任务提交拿他就会一直阻塞 那么我们该如何唤醒他呢
                // 答案就在下面的函数当中 调用线程的 interruput 方法 那么take方法就会产生一个异常 然后我们
                // 捕获到一异常 然后线程退出
            }
        }
    }

    public synchronized void stopWorker() {
        if (isStopped) {
            throw new RuntimeException("thread has been interrupted");
        }
        isStopped = true;
        thisThread.interrupt(); // 中断线程产生异常
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }
}