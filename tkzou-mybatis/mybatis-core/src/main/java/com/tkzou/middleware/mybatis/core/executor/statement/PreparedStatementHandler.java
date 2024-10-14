package com.tkzou.middleware.mybatis.core.executor.statement;

import com.tkzou.middleware.mybatis.core.executor.parameter.ParameterHandler;
import com.tkzou.middleware.mybatis.core.executor.resultset.ResultSetHandler;
import com.tkzou.middleware.mybatis.core.mapping.BoundSql;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * <p> 默认的sql语句处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 23:46
 */
public class PreparedStatementHandler implements StatementHandler {
    /**
     * 核心类，要啥有啥
     */
    private Configuration configuration;
    /**
     * 每个mapper中方法的原材料工厂
     */
    private final MappedStatement ms;
    /**
     * 实际参数
     */
    private final Object parameter;
    /**
     * 参数处理器
     */
    private ParameterHandler parameterHandler;
    /**
     * 结果集处理器
     */
    private ResultSetHandler resultSetHandler;
    /**
     * 解析后的sql，带？的sql，用于预处理
     */
    private BoundSql boundSql;

    public PreparedStatementHandler(Configuration configuration, MappedStatement ms, Object parameter) {
        this.configuration = configuration;
        this.ms = ms;
        this.parameter = parameter;
        //初始化参数处理器
        this.parameterHandler = configuration.newParameterHandler();
        //初始化结果集处理器
        this.resultSetHandler = configuration.newResultSetHandler();
        //解析为带？的sql，用于预处理
        this.boundSql = ms.getBoundSql(parameter);
    }

    @SneakyThrows
    @Override
    public Statement prepare(Connection connection) {
        //即处理带？的sql，提高性能，同时防止sql注入引发的安全问题，但不是本项目的重点，了解即可！
        return connection.prepareStatement(this.boundSql.getSql());
    }

    @Override
    public void parameterize(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        //设置参数
        this.parameterHandler.setParam(ps, this.parameter, this.boundSql.getParameterMappings());
    }

    @SneakyThrows
    @Override
    public <T> T query(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        //处理返回结果，原始结果封装在了ps中
        return (T) this.resultSetHandler.handleResultSets(this.ms, ps);
    }

    @SneakyThrows
    @Override
    public int update(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        //处理结果集，对于更新，返回受影响的行数即可
        return ps.getUpdateCount();
    }

    @Override
    public BoundSql getBoundSql() {
        return this.boundSql;
    }
}
