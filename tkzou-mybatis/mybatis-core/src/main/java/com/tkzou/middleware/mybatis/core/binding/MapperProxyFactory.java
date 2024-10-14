package com.tkzou.middleware.mybatis.core.binding;


import com.tkzou.middleware.mybatis.core.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * <p> mapper代理工厂 </p>
 * 即用于生产mapper接口的代理类
 * 使用的是jdk的动态代理方式
 * 需要使用到MapperProxy对象
 * 思路非常清晰！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/21 19:57
 */
public class MapperProxyFactory {

    /**
     * 拿到mapper接口的代理类
     *
     * @param mapperClass 需要被代理的mapper接口
     * @param sqlSession  操作jdbc的session对象，不要管这个方法为什么要这么设计，
     *                    因为这一点都不重要，无非就是一些基本的重构手法而已！
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> mapperClass, SqlSession sqlSession) {
        /**
         * 第一个参数：类加载器
         * 第二个参数：增强方法所在的类，这个类实现的接口，表示这个代理类可以执行哪些方法。
         * 第三个参数：实现InvocationHandler接口，也即MapperProxy，这个对象非常重要！
         */
        return (T) Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass},
            new MapperProxy(sqlSession, mapperClass));
    }

}
