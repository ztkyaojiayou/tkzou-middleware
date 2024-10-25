package com.tkzou.middleware.threadpool.dtp.v1.core.spring;

import com.tkzou.middleware.threadpool.dtp.v1.common.pool.DtpExecutor;
import com.tkzou.middleware.threadpool.dtp.v1.common.support.ThreadPoolExecutorDecorator;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpExecutorPool;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 后置处理器，每个bean在执行生命周期时都会执行！
 * 我们就可以在这里选取特定类型的bean进行处理，这里则是对动态线程池进行注册到本地（而非ioc容器！）！
 * 注意：DtpExecutor或带@DynamicThreadPool注解的普通线程池需要先成为bean！！！
 *
 * @author zoutongkun
 * @Date 2023/5/20 15:49
 */
@Slf4j
public class DtpBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof Executor)) {
            return bean;
        }
        //
        if (bean instanceof DtpExecutor) {
            //动态线程池，直接纳入管理
            DtpRegistry.registry(beanName, (com.tkzou.middleware.threadpool.dtp.v1.common.pool.DtpExecutor) bean);
        } else {
            //是一个原版线程池，若加了@DynamicThreadPool注解，则也将其包装为动态线程池！
            DtpExecutorPool annotationOnBean = beanFactory.findAnnotationOnBean(beanName, DtpExecutorPool.class);
            if (Objects.isNull(annotationOnBean)) {
                //没加注解，不需要管理
                return bean;
            }
            //把这个原版线程池包装一下
            if (bean instanceof ThreadPoolExecutor) {
                DtpRegistry.registry(beanName, ThreadPoolExecutorDecorator.wrap((ThreadPoolExecutor) bean));
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
