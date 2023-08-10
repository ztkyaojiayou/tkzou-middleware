package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;

/**
 * BeanDefinition注册表接口
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:14
 */
public interface BeanDefinitionRegistry {

    /**
     * 向注册表中注册bean对应的BeanDefinition
     *
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
