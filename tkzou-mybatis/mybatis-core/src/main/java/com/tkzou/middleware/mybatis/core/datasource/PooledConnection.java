package com.tkzou.middleware.mybatis.core.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * <p> 代理连接 </p>
 * 连接池的方式实现
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/27 19:19
 */
public class PooledConnection implements InvocationHandler {
    public static final String CLOSE = "close";
    /**
     * 原连接，也是需要被代理的对象
     * 这里就是原真实的数据库连接
     */
    private Connection connection;
    /**
     * 生成的代理对象
     */
    private Connection proxyConnection;
    /**
     * 自定义的连接池
     * 数据库连接池，用于数据库连接的管理
     */
    private PooledDataSource pooledDataSource;

    public PooledConnection(PooledDataSource pooledDataSource, Connection connection) {
        this.pooledDataSource = pooledDataSource;
        this.connection = connection;
    }

    /**
     * 代理增强逻辑，主要是代理关闭连接的功能！！！
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //单独处理一下close方法，即此时不是真正的关闭，而只是归还连接到连接池中！
        if (method.getName().equals(CLOSE)) {
            this.pooledDataSource.returnConnection(this.proxyConnection);
            return null;
        }
        return method.invoke(this.connection, args);
    }

    /**
     * 获取一个代理连接
     *
     * @return
     */
    public Connection getProxy() {
        Connection proxy = (Connection) Proxy.newProxyInstance(this.connection.getClass().getClassLoader(),
            this.connection.getClass().getInterfaces(), this);
        this.proxyConnection = proxy;
        return proxy;
    }

}
