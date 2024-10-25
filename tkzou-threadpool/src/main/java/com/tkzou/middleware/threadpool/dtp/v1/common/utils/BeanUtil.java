package com.tkzou.middleware.threadpool.dtp.v1.common.utils;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.RejectPolicy;
import com.tkzou.middleware.threadpool.dtp.v1.common.enums.ExecutorType;
import com.tkzou.middleware.threadpool.dtp.v1.common.enums.QueueType;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.concurrent.Executor;

/**
 * 注册bean
 *
 * @author zoutongkun
 * @Date 2023/5/20 15:09
 */
public class BeanUtil {
    public static void registerIfAbsent(BeanDefinitionRegistry registry, ThreadPoolProperties executorProp) {
        register(registry, executorProp.getPoolName(), executorProp);
    }

    public static void register(BeanDefinitionRegistry registry, String beanName, ThreadPoolProperties executorProp) {
        Class<? extends Executor> executorType = ExecutorType.getClazz(executorProp.getPoolType());
        Object[] args = assembleArgs(executorProp);
        register(registry, beanName, executorType, args);
    }

    public static void register(BeanDefinitionRegistry registry, String beanName, Class<?> clazz, Object[] args) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        for (Object arg : args) {
            builder.addConstructorArgValue(arg);
        }
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private static Object[] assembleArgs(ThreadPoolProperties executorProp) {
        return new Object[]{
            executorProp.getCorePoolSize(),
            executorProp.getMaximumPoolSize(),
            executorProp.getKeepAliveTime(),
            executorProp.getTimeUnit(),
            QueueType.getInstance(executorProp.getQueueType(), executorProp.getQueueSize()),
            new NamedThreadFactory(
                executorProp.getPoolName() + executorProp.getThreadFactoryPrefix(),
                executorProp.isDaemon()
            ),
            //先默认不做设置
            RejectPolicy.ABORT.getValue()
        };
    }
}
