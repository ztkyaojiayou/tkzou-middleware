package com.tkzou.middleware.mybatis.spring.transaction;

import com.tkzou.middleware.mybatis.core.transaction.Transaction;
import lombok.SneakyThrows;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * <p> spring事务管理 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/27 22:00
 */
public class SpringManagedTransaction implements Transaction {

    private final DataSource dataSource;
    private Connection connection;
    private boolean autoCommit;

    public SpringManagedTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        //使用spring中配置的连接
        Connection connection = DataSourceUtils.getConnection(this.dataSource);
        this.connection = connection;
        this.autoCommit = connection.getAutoCommit();
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
            this.connection.close();
        }
    }
}
