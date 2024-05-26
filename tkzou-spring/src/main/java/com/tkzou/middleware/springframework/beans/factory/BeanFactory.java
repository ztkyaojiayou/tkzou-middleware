package com.tkzou.middleware.springframework.beans.factory;

import com.tkzou.middleware.springframework.beans.BeansException;

/**
 * bean容器/工厂
 * 这个工厂里面的获取bean的核心逻辑是：
 * 先去ioc容器中取，若没有，则会创建该bean，再把该bean放入ioc容器中
 * 也因此，在如何时候获取bean，都能返回一个bean实例，即使在ioc容器初始化前！！！
 * 这一点至关重要！！！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:17
 */
public interface BeanFactory {

    /**
     * 获取bean
     *
     * @param beanName
     * @return
     * @throws BeansException bean不存在时抛出自定义异常
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 根据名称和类型查找bean
     *
     * @param name
     * @param requiredType
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     * 根据类型查找bean
     *
     * @param requiredType
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;
}