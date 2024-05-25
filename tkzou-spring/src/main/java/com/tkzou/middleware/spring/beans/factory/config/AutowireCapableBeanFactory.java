package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.factory.BeanFactory;

/**
 * 不知道是干嘛，先写着！
 * 原来是为了⽅便后⾯的讲解和功能实现，并且尽量保持和spring中BeanFactory的继承层次⼀致，
 * 对BeanFactory的继承层次稍微做了调整。
 * 先就这么着！
 * 更新：执行BeanPostProcessors中的两个方法
 *
 * @author zoutongkun
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 执行BeanPostProcessors的postProcessBeforeInitialization方法
     *
     * @param existingBean
     * @param beanName
     * @return
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName);

    /**
     * 执行BeanPostProcessors的postProcessAfterInitialization方法
     *
     * @param existingBean
     * @param beanName
     * @return
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName);
}
