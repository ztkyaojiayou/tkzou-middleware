package com.tkzou.middleware.mybatis.core.transaction;

import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * <p> JDBC事务 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/27 22:00
 */
public class JdbcTransaction implements Transaction {
    /**
     * 数据源
     */
    private DataSource dataSource;
    /**
     * 保存获取到的数据库连接
     */
    private Connection connection;
    /**
     * 是否自动提交
     */
    private boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(this.autoCommit);
        //保存一下这个连接，相当于初始化了，
        //这样该类的其他地方就可以使用啦!
        this.connection = connection;
        return connection;
    }

    @SneakyThrows
    @Override
    public void commit() {
        if (this.autoCommit) {
            return;
        }
        if (this.connection != null) {
            this.connection.commit();
        }
    }

    @SneakyThrows
    @Override
    public void rollback() {
        if (this.autoCommit) {
            return;
        }
        if (this.connection != null) {
            this.connection.rollback();
        }
    }

    @SneakyThrows
    @Override
    public void close() {
        if (this.connection != null) {
            //是个PooledConnection，也即是被代理之后的连接!
            //因此这里是优先归还连接
            this.connection.close();
        }
    }
}
