package com.tkzou.middleware.springframework.beans.factory.support;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;

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


    /**
     * 根据beanName查找BeanDefinition
     * @param beanName
     * @return
     * @throws BeansException
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;


    /**
     * 是否包含指定beaName的BeanDefinition
     *
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 返回定义的所有bean的名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();
}
