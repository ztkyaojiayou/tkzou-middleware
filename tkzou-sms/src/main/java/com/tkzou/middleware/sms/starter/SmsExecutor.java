package com.tkzou.middleware.sms.starter;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BeanFactory
 * <p> 单例bean工具管理,确保spring中管理的对象在这里都能拿到
 *
 * @author :zoutongkun
 * 2024/4/7  15:26
 **/
@Configuration
@Data
public class SmsExecutor {
    /**
     * 线程池
     */
    private static Executor executor;

    /**
     * 初始化一个线程池
     * 此时若没有SmsCommonConfig，则会先初始化SmsCommonConfig
     *
     * @param config
     * @return
     */
    @Bean
    public Executor smsExecutor(SmsCommonConfig config) {
        ThreadPoolExecutor ex = null;
        if (executor == null) {
            // 创建一个线程池对象
            ex = new ThreadPoolExecutor(
                    config.getCorePoolSize(),
                    config.getMaxPoolSize(),
                    config.getQueueCapacity(),
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(config.getMaxPoolSize()));
            // 线程池对拒绝任务的处理策略,当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
            ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor = ex;
        }
        return ex;
    }

    /**
     * 获取单例线程池
     * 其实可以直接通过@Autowired到ioc容器去取。
     *
     * @return
     */
    public static Executor getExecutor() {
        return executor;
    }

}
