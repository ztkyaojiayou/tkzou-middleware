package com.tkzou.middleware.threadpool.simple.v2;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 线程工作类
 * 核心功能就是通过一个while死循环来不断消费任务队列中的任务
 *
 * @author zoutongkun
 */
public class Worker implements Runnable {

    /**
     * 用于保存任务的队列
     */
    private ArrayBlockingQueue<Runnable> tasks;
    /**
     * 线程的状态 是否终止
     */
    private volatile boolean isStopped;

    /**
     * 保存执行 run 方法的线程
     */
    private volatile Thread thisThread;

    public Worker(ArrayBlockingQueue<Runnable> tasks) {
        // 这个参数是线程池当中传入的
        this.tasks = tasks;
    }

    @Override
    public void run() {
        thisThread = Thread.currentThread();
        //使用一个while死循环消费队列中的任务
        //通过isStopped标志位来控制线程的启停，这也是常用的关闭线程的优雅方式！
        while (!isStopped) {
            try {
                //执行任务
                Runnable task = tasks.take();
                task.run();
            } catch (InterruptedException e) {
                // do nothing
                System.out.println("当前线程被中断啦，但我不响应！");
            }
        }
    }

    /**
     * 注意是其他线程调用这个方法 同时需要注意是 thisThread 这个线程在执行上面的 run 方法
     * 其他线程调用 thisThread 的 interrupt 方法之后 thisThread 会出现异常 然后就不会一直阻塞了
     * 会判断 isStopped 是否为 true 如果为 true 的话就可以退出 while 循环了
     */
    public void stop() {
        isStopped = true;
        thisThread.interrupt(); // 中断线程 thisThread
    }

    /**
     * 判断当前线程是否已停止
     *
     * @return
     */
    public boolean isStopped() {
        return isStopped;
    }
}