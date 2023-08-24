package com.tkzou.middleware.spring.test.ioc.common;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.spring.test.ioc.bean.Car;

/**
 * 自定义实现类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 18:53
 */
public class CustomerBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("初始化之前先执行postProcessBeforeInitialization-----");
        //修改一下这个bean的属性
        if ("car".equals(beanName)) {
            ((Car) bean).setBrand("new-brand666");
        }
        //再返回这个bean
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("初始化之后再执行postProcessAfterInitialization-----");
        return bean;
    }
}
