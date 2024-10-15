package com.tkzou.middleware.mybatis.core.datasource;

import cn.hutool.core.collection.CollectionUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * <p> 数据库连接池 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/27 19:13
 */
@Slf4j
public class PooledDataSource implements DataSource {
    /**
     * mysql连接参数
     */
    public static final String MYSQL_URL = "jdbc:mysql://127.0.0" +
        ".1:3306/mybatis-tkzou?useUnicode=true&characterEncoding=UTF8&useSSL=false";
    public static final String MYSQL_USER_NAME = "root";
    public static final String MYSQL_PASS_WORD = "root";
    /**
     * 基础连接数
     */
    private final int POOL_SIZE = 10;
    /**
     * 连接池
     * 保存数据库连接的队列，是个并发队列，保证线程安全
     * 但要注意的是，此时的数据库已经是被代理过后的了，也即PooledConnection
     */
    private final LinkedBlockingQueue<Connection> CONNECTION_POOL = new LinkedBlockingQueue<>(this.POOL_SIZE);

    /**
     * new时就初始化基础连接数，但要注意：这里获取的都是代理连接，因为要具备close时是归还连接的功能！
     * 常规套路，反正就那么几个地方初始化
     * 即：main函数中、类初始化时、对象初始化时以及springboot的各种扩展点。
     */
    @SneakyThrows
    public PooledDataSource() {
        //初始化指定数量的数据库连接
        for (int i = 0; i < this.POOL_SIZE; i++) {
            Connection connection = DriverManager.getConnection(MYSQL_URL,
                MYSQL_USER_NAME, MYSQL_PASS_WORD);
            //获取代理连接并添加到连接池中！
            this.CONNECTION_POOL.add(new PooledConnection(this, connection).getProxy());
        }
    }

    /**
     * 从连接池中获取一个代理连接
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        //优先从数据库连接池中获取
        if (CollectionUtil.isNotEmpty(this.CONNECTION_POOL)) {
            //取出，同时会删除！
            return this.CONNECTION_POOL.poll();
        } else {
            //若没有，则重新创建连接，此时获取的是原始连接
            Connection connection = DriverManager.getConnection(MYSQL_URL,
                MYSQL_USER_NAME, MYSQL_PASS_WORD);
            //转为代理连接！
            Connection newConnection = new PooledConnection(this, connection).getProxy();
            this.CONNECTION_POOL.add(newConnection);
            return newConnection;
        }
    }

    /**
     * 归还连接，而不是真正关闭连接
     * 但若超过了指定数量的连接，才真正关闭
     *
     * @param connection
     */
    public void returnConnection(Connection connection) {
        try {
            //优先归还
            if (this.CONNECTION_POOL.size() < this.POOL_SIZE) {
                this.CONNECTION_POOL.add(connection);
            } else {
                //若连接池满了，则再关闭
                connection.close();
            }
        } catch (Exception e) {
            log.error("归还连接失败", e);
        }
    }


    @Override
    public Connection getConnection(String username, String password) {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
