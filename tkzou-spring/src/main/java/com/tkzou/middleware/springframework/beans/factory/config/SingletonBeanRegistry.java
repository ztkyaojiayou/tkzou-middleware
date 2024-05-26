package com.tkzou.middleware.springframework.beans.factory.config;

/**
 * 单例bean的获取接口
 * 注意：这里将单例bean的获取和一般bean的获取接口（也即BeanFactory）分开了，
 * 虽然我们这里的bean其实就是单例bean！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 13:43
 */
public interface SingletonBeanRegistry {

    /**
     * 获取单例bean对象
     *
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);

    /**
     * 添加单例bean对象
     * 通常用于手动直接将这个对象注册到ioc中
     * 不经历bean的生命周期，一般用于在bean的初始化前对一些核心组件进行初始化！
     *
     * @param beanName
     * @param singletonObject
     */
    void addSingleton(String beanName, Object singletonObject);

}
