package com.tkzou.middleware.spring;

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
}