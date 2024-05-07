package com.tkzou.middleware.threadpool.simple.v4;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jdk线程池实现--推荐
 *
 * @author zoutongkun
 */
public class ThreadPool {
    /**
     * 当前在执行任务的线程个数
     */
    private AtomicInteger ct = new AtomicInteger(0);
    /**
     * 核心线程数
     */
    private final int corePoolSize;
    /**
     * 最大线程数
     */
    private final int maximumPoolSize;
    /**
     * 空闲线程的保活时长
     */
    private final long keepAliveTime;
    /**
     * 保活时长单位
     */
    private final TimeUnit unit;
    /**
     * 存放任务的等待队列
     */
    private final BlockingQueue<Runnable> taskQueue;
    /**
     * 拒绝策略
     * 默认为拒接
     */
    private final RejectPolicy policy;
    /**
     * 执行任务的worker，本质就是个线程任务
     * 里面会封装执行该任务的线程
     */
    private final ArrayList<Worker> workers = new ArrayList<>();
    /**
     * 线程池的状态，是否停止，默认false
     */
    private volatile boolean isStopped;
    /**
     * 主要是用于表示是否使用上面的 keepAliveTime 和 unit，
     * 如果使用就是在一定的时间内，如果没有从任务队列当中获取到任务，
     * 线程就从线程池退出，但是需要保证线程池当中最小的线程个数不小于 corePoolSize 。
     */
    private final boolean useTimed;

    /**
     * 获取当前在执行任务的线程个数
     *
     * @return
     */
    public int getCt() {
        return ct.get();
    }

    /**
     * 构造器
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param unit
     * @param keepAliveTime
     * @param policy
     * @param maxTasks
     */
    public ThreadPool(int corePoolSize, int maximumPoolSize, TimeUnit unit, long keepAliveTime, RejectPolicy policy
            , int maxTasks) {
        // please add -ea to vm options to make assert keyword enable
        assert corePoolSize > 0;
        assert maximumPoolSize > 0;
        assert keepAliveTime >= 0;
        assert maxTasks > 0;

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.unit = unit;
        this.policy = policy;
        this.keepAliveTime = keepAliveTime;
        taskQueue = new ArrayBlockingQueue<>(maxTasks);
        useTimed = keepAliveTime != 0;
    }

    /**
     * 添加worker
     *
     * @param runnable      需要被执行的任务
     * @param isMaxPoolSize 是否使用 maximumPoolSize
     * @return boolean
     */
    public synchronized boolean addWorker(Runnable runnable, boolean isMaxPoolSize) {
        if (ct.get() >= corePoolSize && !isMaxPoolSize) {
            return false;
        }
        if (ct.get() >= maximumPoolSize && isMaxPoolSize) {
            return false;
        }
        //为该任务创建worker
        Worker worker = new Worker(runnable);
        workers.add(worker);
        //启用一个线程来执行该worker
        Thread thread = new Thread(worker, "ThreadPool-" + "Thread-" + ct.addAndGet(1));
        thread.start();
        return true;
    }

    /**
     * 向线程池提交任务
     * 不带返回值
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void execute(Runnable runnable) throws InterruptedException {
        //检查线程池的状态
        checkPoolState();
        // 如果能够加入新的线程执行任务 加入成功就直接返回
        if (addWorker(runnable, false)
                // 如果 taskQueue.offer(runnable) 返回 false 说明提交任务失败 任务队列已经满了
                || !taskQueue.offer(runnable)
                // 使用能够使用的最大的线程数 (maximumPoolSize) 看是否能够产生新的线程
                || addWorker(runnable, true)) {
            return;
        }

        // 如果任务队列满了而且不能够加入新的线程 则拒绝这个任务
        if (!taskQueue.offer(runnable)) {
            //拒绝
            reject(runnable);
        }
    }

    /**
     * 拒绝逻辑
     *
     * @param runnable
     * @throws InterruptedException
     */
    private void reject(Runnable runnable) throws InterruptedException {
        switch (policy) {
            case ABORT:
                //抛异常
                throw new RuntimeException("task queue is full");
            case CALLER_RUN:
                //调用主线程来执行该任务
                runnable.run();
                return;
            case DISCARD:
                //直接丢弃
                return;
            case DISCARD_OLDEST:
                // 放弃等待时间最长的任务，继续执行当前任务
                taskQueue.poll();
                execute(runnable);
                return;
            default:
        }
    }

    /**
     * 检查线程池的状态
     * 即检查当前线程池是否已经停止
     */
    private void checkPoolState() {
        if (isStopped) {
            // 如果线程池已经停下来了，就不在向任务队列当中提交任务了
            throw new RuntimeException("thread pool has been stopped, so quit submitting task");
        }
    }

    /**
     * 提交任务到线程池
     * 带返回值
     *
     * @param task
     * @param <V>
     * @return
     * @throws InterruptedException
     */
    public <V> RunnableFuture<V> submit(Callable<V> task) throws InterruptedException {
        checkPoolState();
        FutureTask<V> futureTask = new FutureTask<>(task);
        execute(futureTask);
        return futureTask;
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
     * 等待所有任务执行完毕
     */
    private void waitForAllTasks() {
        // 当线程池当中还有任务的时候 就不退出循环
        while (taskQueue.size() > 0) {
            Thread.yield();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * worker，本质就是个线程任务，里面会封装执行该任务的线程
     * 易知是以任务为主体，而执行这个任务的线程是不固定的！
     */
    class Worker implements Runnable {
        /**
         * 执行当前worker的线程
         */
        private Thread thisThread;
        /**
         * 第一个任务
         */
        private final Runnable firstTask;
        /**
         * 当前worker是否停止
         */
        private volatile boolean isStopped;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        /**
         * 任务逻辑
         */
        @Override
        public void run() {
            // 先执行传递过来的第一个任务 这里是一个小的优化 让线程直接执行第一个任务 不需要
            // 放入任务队列再取出来执行了
            firstTask.run();
            //执行当前worker的线程
            thisThread = Thread.currentThread();
            //也是使用一个死循环来不断从任务队列中执行任务
            while (!isStopped) {
                try {
                    Runnable task = useTimed ? taskQueue.poll(keepAliveTime, unit) : taskQueue.take();
                    if (task == null) {
                        int i;
                        boolean exit = true;
                        if (ct.get() > corePoolSize) {
                            do {
                                i = ct.get();
                                if (i <= corePoolSize) {
                                    exit = false;
                                    break;
                                }
                            } while (!ct.compareAndSet(i, i - 1));
                            if (exit) {
                                return;
                            }
                        }
                    } else {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }

        /**
         * 停止当前worker
         */
        public synchronized void stopWorker() {
            if (isStopped) {
                throw new RuntimeException("thread has been interrupted");
            }
            isStopped = true;
            thisThread.interrupt();
        }

    }

}