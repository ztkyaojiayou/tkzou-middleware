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
 * <p> 预编译语句处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 23:46
 */
public class PreparedStatementHandler implements StatementHandler {

    private Configuration configuration;
    private MappedStatement ms;
    private Object parameter;
    private ParameterHandler parameterHandler;
    private ResultSetHandler resultSetHandler;
    private BoundSql boundSql;

    public PreparedStatementHandler(Configuration configuration, MappedStatement ms, Object parameter) {
        this.configuration = configuration;
        this.ms = ms;
        this.parameter = parameter;
        this.parameterHandler = configuration.newParameterHandler();
        this.resultSetHandler = configuration.newResultSetHandler();
        this.boundSql = ms.getBoundSql(parameter);
    }

    @SneakyThrows
    @Override
    public Statement prepare(Connection connection) {
        return connection.prepareStatement(this.boundSql.getSql());
    }

    @Override
    public void parameterize(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        this.parameterHandler.setParam(ps, this.parameter, this.boundSql.getParameterMappings());
    }

    @SneakyThrows
    @Override
    public <T> T query(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return (T) this.resultSetHandler.handleResultSets(this.ms, ps);
    }

    @SneakyThrows
    @Override
    public int update(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ps.getUpdateCount();
    }

    @Override
    public BoundSql getBoundSql() {
        return this.boundSql;
    }
}
