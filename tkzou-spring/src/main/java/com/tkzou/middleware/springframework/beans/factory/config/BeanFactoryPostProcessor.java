package com.tkzou.middleware.springframework.beans.factory.config;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;

/**
 * 用于自定义修改beanDefinition的属性值
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 18:35
 */
public interface BeanFactoryPostProcessor {

    /**
     * 用于修改beanDefinition属性值
     * 时机：在所有的beanDefinition加载完成后，在bean实例化之前，
     *
     * @param beanFactory
     * @throws BeansException
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
