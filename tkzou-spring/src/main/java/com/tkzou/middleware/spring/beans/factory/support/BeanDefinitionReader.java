package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.core.io.Resource;
import com.tkzou.middleware.spring.core.io.ResourceLoader;

/**
 * 读取bean定义信息也即BeanDefinition的接口
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/23 17:52
 */
public interface BeanDefinitionReader {

    /**
     * 获取BeanDefinitionRegistry
     *
     * @return
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取ResourceLoader
     *
     * @return
     */
    ResourceLoader getResourceLoader();

    /**
     * 加载BeanDefinition的重载方法
     *
     * @param resource
     * @throws BeansException
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 加载BeanDefinition的重载方法
     *
     * @param location
     * @throws BeansException
     */
    void loadBeanDefinitions(String location) throws BeansException;

    /**
     * 加载BeanDefinition的重载方法
     *
     * @param locations
     * @throws BeansException
     */
    void loadBeanDefinitions(String[] locations) throws BeansException;
}
