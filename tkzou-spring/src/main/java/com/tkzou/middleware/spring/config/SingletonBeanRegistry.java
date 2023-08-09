package com.tkzou.middleware.spring.config;

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
}
