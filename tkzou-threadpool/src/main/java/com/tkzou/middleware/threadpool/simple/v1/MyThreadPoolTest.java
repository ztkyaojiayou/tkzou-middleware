package com.tkzou.middleware.threadpool.simple.v1;

import java.util.stream.IntStream;

/**
 * 测试类
 *
 * @author zoutongkun
 */
public class MyThreadPoolTest {

    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(10);
        IntStream.range(0, 100).forEach((i) -> myThreadPool.execute(() -> System.out.println(Thread.currentThread().getName() + "--->> Hello MyThreadPool,当前任务的序号为：" + i)));
    }
}