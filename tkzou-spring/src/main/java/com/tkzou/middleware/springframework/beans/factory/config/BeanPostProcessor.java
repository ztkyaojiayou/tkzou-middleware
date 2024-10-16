package com.tkzou.middleware.springframework.beans.factory.config;

import com.tkzou.middleware.springframework.beans.BeansException;

/**
 * 后置处理器，它有多种，本质就是在bean创建的各个过程中作织入，就是这么简单！！！
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
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
