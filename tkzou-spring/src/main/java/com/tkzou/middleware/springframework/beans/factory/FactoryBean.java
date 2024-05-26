package com.tkzou.middleware.springframework.beans.factory;

/**
 * FactoryBean接口定义
 *
 * @author zoutongkun
 */
public interface FactoryBean<T> {
    /**
     * 获取实际的bean
     * 通常是使用反射机制来创建复杂的bean，
     * 如mybatis中的对mapper接口的代理对象的创建！
     * 再如feign中对远程接口的代理对象的创建！
     *
     * @return
     * @throws Exception
     */
    T getObject() throws Exception;

    /**
     * 当前bean是否为单例
     * 由FactoryBean的实现类自己决定
     * 默认情况下，返回true
     *
     * @return
     */
    default boolean isSingleton() {
        return true;
    }

    /**
     * 获取bean的实际类型
     *
     * @return
     */
    Class<?> getObjectType();
}
