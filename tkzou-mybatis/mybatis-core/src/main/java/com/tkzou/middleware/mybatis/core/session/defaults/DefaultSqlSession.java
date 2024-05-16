package com.tkzou.middleware.mybatis.core.session.defaults;

import com.tkzou.middleware.mybatis.core.binding.MapperProxyFactory;
import com.tkzou.middleware.mybatis.core.executor.Executor;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.session.SqlSession;

import java.util.List;

/**
 * <p> 默认SqlSession </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 01:24
 */
public class DefaultSqlSession implements SqlSession {
    /**
     * 也传入这个核心类，原材料应有尽有
     */
    private Configuration configuration;
    /**
     * 传入执行器
     */
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public int insert(String statementId, Object parameter) {
        MappedStatement ms = this.configuration.getMappedStatement(statementId);
        return this.executor.update(ms, parameter);
    }

    @Override
    public int delete(String statementId, Object parameter) {
        MappedStatement ms = this.configuration.getMappedStatement(statementId);
        return this.executor.update(ms, parameter);
    }

    @Override
    public int update(String statementId, Object parameter) {
        MappedStatement ms = this.configuration.getMappedStatement(statementId);
        return this.executor.update(ms, parameter);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        //基于selectList方法即可
        List<T> list = this.selectList(statementId, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <T> List<T> selectList(String statementId, Object parameter) {
        //通过statementId就可以get到MappedStatement啦！
        MappedStatement ms = this.configuration.getMappedStatement(statementId);
        return this.executor.query(ms, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> mapper) {
        return MapperProxyFactory.getProxy(mapper, this);
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void commit() {
        this.executor.commit();
    }

    @Override
    public void rollback() {
        this.executor.rollback();
    }

    @Override
    public void close() {
        this.executor.close();
    }

}
