package com.tkzou.middleware.spring.beans.factory;

import com.tkzou.middleware.spring.beans.BeansException;

import java.util.Map;

/**
 * bean工厂接口的另一个子接口
 * 目前还不知道哪里有使用到它，先写！
 * 定义了两个关于bean的方法
 *
 * @author :zoutongkun
 * @date :2023/8/23 10:35 下午
 * @description :
 * @modyified By:
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 获取指定类型的所有bean实例
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 获取所有bean的名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();
}
