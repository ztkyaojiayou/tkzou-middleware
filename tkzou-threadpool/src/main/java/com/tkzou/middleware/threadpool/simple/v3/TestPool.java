package com.tkzou.middleware.threadpool.simple.v3;

/**
 * 测试类
 *
 * @author zoutongkun
 */
public class TestPool {

    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new ThreadPool(3, 1024);

        for (int i = 0; i < 10; i++) {
            int tmp = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " say hello " + tmp);
            });
        }
        pool.shutDown();
    }
}