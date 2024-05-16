package com.tkzou.middleware.mybatis.core.executor;

import com.tkzou.middleware.mybatis.core.cache.Cache;
import com.tkzou.middleware.mybatis.core.cache.PerpetualCache;
import com.tkzou.middleware.mybatis.core.executor.statement.StatementHandler;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.transaction.Transaction;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * <p> 简单执行器 </p>
 * 封装了一级缓存的管理！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 23:10
 */
public class SimpleExecutor implements Executor {
    /**
     * 所有mapper接口的元信息，核心类
     */
    private Configuration configuration;
    /**
     * 事务，里面就有数据库连接池！
     */
    private Transaction transaction;
    /**
     * 一级缓存，不跨session
     * 也即使用PerpetualCache
     */
    private Cache localCache;

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        //使用PerpetualCache
        this.localCache = new PerpetualCache("LocalCache");
    }

    @SneakyThrows
    @Override
    public <T> List<T> query(MappedStatement ms, Object parameter) {
        //先取一级缓存key
        String cacheKey = ms.createCacheKey(parameter);
        //先从一级缓存中取数据
        Object list = this.localCache.getObject(cacheKey);
        //强转为泛型
        if (list != null) {
            return (List<T>) list;
        }
        StatementHandler statementHandler = this.configuration.newStatementHandler(ms, parameter);
        Statement statement = this.prepareStatement(statementHandler);
        // 缓存没有时再查库
        list = statementHandler.query(statement);
        this.localCache.putObject(cacheKey, list);
        //同理，强转为泛型
        return (List<T>) list;
    }


    @SneakyThrows
    @Override
    public int update(MappedStatement ms, Object parameter) {
        this.localCache.clear();
        StatementHandler statementHandler = this.configuration.newStatementHandler(ms, parameter);
        Statement statement = this.prepareStatement(statementHandler);
        return statementHandler.update(statement);
    }

    @Override
    public void commit() {
        this.transaction.commit();
    }

    @Override
    public void rollback() {
        this.transaction.rollback();
    }

    @Override
    public void close() {
        this.transaction.close();
    }

    private Statement prepareStatement(StatementHandler statementHandler) {
        Connection connection = this.getConnection();
        Statement statement = statementHandler.prepare(connection);
        statementHandler.parameterize(statement);
        return statement;
    }


    @SneakyThrows
    private Connection getConnection() {
        return this.transaction.getConnection();
    }


}
