package com.tkzou.middleware.spring.context.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.spring.context.ApplicationContext;
import com.tkzou.middleware.spring.context.ApplicationContextAware;

/**
 * 专门用于感知ApplicationContext接口
 * 即处理实现了该接口的bean，将ApplicationContext赋值到bean中
 *
 * @author :zoutongkun
 * @date :2024/5/23 9:58 下午
 * @description :
 * @modyified By:
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    /**
     * 构造器，那么关键就是看在哪里初始化了，看在哪里调用了它
     *
     * @param applicationContext
     */
    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //处理实现了ApplicationContextAware接口的bean
        if (bean instanceof ApplicationContextAware) {
            //把当前容器对象赋值到该bean的字段中
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //该方法目前不做任何处理，原样返回
        return bean;
    }
}
