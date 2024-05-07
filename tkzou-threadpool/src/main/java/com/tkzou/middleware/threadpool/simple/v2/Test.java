package com.tkzou.middleware.threadpool.simple.v2;

/**
 * 测试类
 *
 * @author zoutongkun
 */
public class Test {

    public static void main(String[] args) {
        // 开启5个线程 任务队列当中最多只能存1024个任务
        MyFixedThreadPool pool = new MyFixedThreadPool(5, 1024);
        for (int i = 0; i < 100; i++) {
            pool.submit(() -> {
                // 提交的任务就是打印线程自己的名字
                System.out.println(Thread.currentThread().getName());
            });
        }
        pool.shutDown();
    }
}