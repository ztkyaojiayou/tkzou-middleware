package com.tkzou.middleware.threadpool.simple.v2;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 固定线程数的线程池实现
 *
 * @author zoutongkun
 */
public class MyFixedThreadPool {

    /**
     * 用于存储任务的阻塞队列
     */
    private ArrayBlockingQueue<Runnable> taskQueue;

    /**
     * 保存线程池当中所有的线程
     */
    private ArrayList<Worker> threadLists;

    /**
     * 线程池是否关闭
     */
    private boolean isShutDown;

    /**
     * 线程池当中的线程数目
     */
    private int numThread;

    /**
     * 构造器
     *
     * @param i
     */
    public MyFixedThreadPool(int i) {
        this(Runtime.getRuntime().availableProcessors() + 1, 1024);
    }

    /**
     * 构造器
     *
     * @param numThread     核心线程数
     * @param maxTaskNumber 最大任务数
     */
    public MyFixedThreadPool(int numThread, int maxTaskNumber) {
        this.numThread = numThread;
        // 创建阻塞队列
        taskQueue = new ArrayBlockingQueue<>(maxTaskNumber);
        threadLists = new ArrayList<>();
        // 将所有的 worker 都保存下来
        for (int i = 0; i < numThread; i++) {
            Worker worker = new Worker(taskQueue);
            threadLists.add(worker);
        }
        //创建指定个数的线程，同时启动线程，消费队列中的任务
        for (int i = 0; i < threadLists.size(); i++) {
            // 让worker开始工作
            new Thread(threadLists.get(i),
                    "ThreadPool-Thread-" + i).start();
        }
    }

    /**
     * 停止所有的 worker 这个只在线程池要关闭的时候才会调用
     */
    private void stopAllThread() {
        for (Worker worker : threadLists) {
            worker.stop(); // 调用 worker 的 stop 方法 让正在执行 worker 当中 run 方法的线程停止执行
        }
    }

    /**
     * 关闭线程池
     */
    public void shutDown() {
        // 等待任务队列当中的任务执行完成
        while (taskQueue.size() != 0) {
            // 如果队列当中还有任务 则让出 CPU 的使用权
            Thread.yield();
        }
        // 在所有的任务都被执行完成之后 停止所有线程的执行
        stopAllThread();
    }

    /**
     * 提交任务到线程池
     *
     * @param runnable
     */
    public void submit(Runnable runnable) {
        try {
            // 如果任务队列满了， 调用这个方法的线程会被阻塞
            taskQueue.put(runnable);
        } catch (InterruptedException e) {
            System.out.println("当前线程被中断啦，但我不响应！");
        }
    }
}