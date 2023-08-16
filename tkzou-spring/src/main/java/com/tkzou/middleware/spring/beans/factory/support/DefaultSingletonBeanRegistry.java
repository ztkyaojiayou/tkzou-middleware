package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取单例对象默认实现类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:02
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    /**
     * 使用map存放单例对象
     * 因此易知，需要先将所有的单例bean对象存入
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 注册单例对象到map中
     * 注意：protected修饰符的作用范围为：相同包下可自由访问
     *
     * @param beanName
     * @param singletonObject
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    /**
     * 从map中获取单例对象
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }
}
