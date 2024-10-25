package com.tkzou.middleware.threadpool.dtp.v1.config;

import com.tkzou.middleware.threadpool.dtp.v1.common.constant.DtpConfigConstant;
import com.tkzou.middleware.threadpool.dtp.v1.common.enums.ExecutorType;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author zoutongkun
 * @Date 2023/5/20 14:46
 */
@Data
public class ThreadPoolProperties {
    private String poolName;
    private String poolType = ExecutorType.COMMON.getType();

    /**
     * 是否为守护线程
     */
    private boolean isDaemon = false;

    /**
     * 以下都是核心参数
     */
    private int corePoolSize = 1;
    private int maximumPoolSize = 1;
    private long keepAliveTime;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private String queueType = "arrayBlockingQueue";
    private int queueSize = 5;
    private String threadFactoryPrefix = DtpConfigConstant.THREAD_FACTORY_PREFIX;
    private String rejectedExecutionHandler;
}
