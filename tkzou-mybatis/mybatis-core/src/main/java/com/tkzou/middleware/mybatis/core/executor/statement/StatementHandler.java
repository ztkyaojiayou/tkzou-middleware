package com.tkzou.middleware.mybatis.core.executor.statement;

import com.tkzou.middleware.mybatis.core.mapping.BoundSql;

import java.sql.Connection;
import java.sql.Statement;

/**
 * <p> 语句处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 23:44
 */
public interface StatementHandler {

    Statement prepare(Connection connection);

    void parameterize(Statement statement);

    <T> T query(Statement statement);

    int update(Statement statement);

    BoundSql getBoundSql();

}
