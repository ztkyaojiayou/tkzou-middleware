package com.tkzou.middleware.threadpool.dtp.v1.test.config;

import com.tkzou.middleware.threadpool.dtp.v1.common.pool.DtpExecutor;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpExecutorPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zoutongkun
 * @Date 2023/5/20 17:18
 */
@Configuration
public class TestConfiguration {
    @Bean
    public DtpExecutor dtpExecutor3() {
        return new DtpExecutor(3,
            3,
            100,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100));
    }

    @Bean
    @DtpExecutorPool
    public ThreadPoolExecutor dtpExecutor44() {
        return new ThreadPoolExecutor(3,
            3,
            100,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100));
    }
}
