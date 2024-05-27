package com.tkzou.middleware.springframework.beans.factory;

import com.tkzou.middleware.springframework.beans.BeansException;

/**
 * 三级缓存
 * 是个接口，是个对象工厂
 * 或者还可以理解为是个函数式接口！
 *
 * @author zoutongkun
 */
public interface ObjectFactory<T> {
    /**
     * 创建需要提前暴露的对象！！！
     * 可能是代理对象，也可能是原始对象
     *
     * @return
     * @throws BeansException
     */
    T getObject() throws BeansException;
}