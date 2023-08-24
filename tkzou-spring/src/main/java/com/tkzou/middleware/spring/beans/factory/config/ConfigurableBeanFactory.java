package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.factory.HierarchicalBeanFactory;

/**
 * 不知道是干嘛，先写着！
 * 更新：提供添加/扫描/注册BeanPostProcessor
 *
 * @author zoutongkun
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * 添加/扫描/注册BeanPostProcessor
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}
