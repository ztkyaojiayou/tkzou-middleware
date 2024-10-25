package com.tkzou.middleware.threadpool.dtp.v1.core.core;

import com.tkzou.middleware.threadpool.dtp.v1.common.pool.DtpExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zoutongkun
 * @Date 2023/5/21 15:51
 */
public class DtpThreadPoolCreator {
    public static ThreadPoolExecutor createNormalExecutor(String poolName) {
        DtpExecutor executor = new DtpExecutor(
            5,
            10,
            10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50));
        DtpRegistry.registry(poolName, executor);
        return executor;
    }
}
