package com.tkzou.middleware.spring.beans.factory;

import com.tkzou.middleware.spring.beans.BeansException;

/**
 * bean容器/工厂
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
}