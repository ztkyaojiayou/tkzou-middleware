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
 * 实现crud
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
        //获取sql语句处理器
        StatementHandler statementHandler = this.configuration.newStatementHandler(ms, parameter);
        //sql语句预处理并设置好参数
        Statement statement = this.prepareStatement(statementHandler);
        //查询
        list = statementHandler.query(statement);
        //再设置到缓存中
        this.localCache.putObject(cacheKey, list);
        //最后返回
        //同理，强转为泛型
        return (List<T>) list;
    }


    @SneakyThrows
    @Override
    public int update(MappedStatement ms, Object parameter) {
        //先清空一级缓存
        this.localCache.clear();
        //sql语句预处理
        StatementHandler statementHandler = this.configuration.newStatementHandler(ms, parameter);
        Statement statement = this.prepareStatement(statementHandler);
        //执行sql
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

    /**
     * sql语句预处理
     * 解析为带？的sql并设置参数
     *
     * @param statementHandler
     * @return
     */
    private Statement prepareStatement(StatementHandler statementHandler) {
        //获取连接
        Connection connection = this.getConnection();
        //sql预处理，此时会将原始sql中的参数变为？
        Statement statement = statementHandler.prepare(connection);
        //设置参数，即把原始sql中的#{user.name}替换为实际传入的参数，此时万事俱备了！
        statementHandler.parameterize(statement);
        return statement;
    }

    /**
     * 获取sql连接
     *
     * @return
     */
    @SneakyThrows
    private Connection getConnection() {
        return this.transaction.getConnection();
    }


}
