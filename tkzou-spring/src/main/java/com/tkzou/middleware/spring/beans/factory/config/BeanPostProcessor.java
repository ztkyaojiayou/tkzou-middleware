package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.BeansException;

/**
 * 用于修改实例化后的bean的修改扩展点
 * todo 那么初始化方法是哪个? invokeInitMethods
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 18:27
 */
public interface BeanPostProcessor {

    /**
     * 在bean执行初始化方法之前执行该方法
     * 此时bean已经完成了实例化！
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在bean执行初始化方法之后执行该方法
     * 此时bean已经完成了实例化！
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
