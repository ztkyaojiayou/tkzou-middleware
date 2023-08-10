package com.tkzou.middleware.spring.support;

import com.tkzou.middleware.spring.BeansException;
import com.tkzou.middleware.spring.config.BeanDefinition;

/**
 * Bean的实例化策略
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 16:28
 */
public interface InstantiationStrategy {

    /**
     * 实例化bean的方式（都是基于bean的class对象）
     * 目前实现了两种策略
     * 1.SimpleInstantiationStrategy，使⽤bean的构造函数来实例化
     * 2.CglibSubclassingInstantiationStrategy，使⽤CGLIB动态⽣成⼦类
     *
     * @param beanDefinition
     * @return
     * @throws BeansException
     */
    Object instantiate(BeanDefinition beanDefinition) throws BeansException;
}
