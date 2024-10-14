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
    /**
     * 预处理
     *
     * @param connection
     * @return
     */
    Statement prepare(Connection connection);

    /**
     * 设置sql参数
     * 即把原始sql中的#{user.name}替换为实际传入的参数
     *
     * @param statement
     */
    void parameterize(Statement statement);

    /**
     * 查询，和sql交互进行查询，该过程不重要了！
     * 重点在于返回值的处理，
     *
     * @param statement
     * @param <T>
     * @return
     */
    <T> T query(Statement statement);

    /**
     * 更新（包括增删改）
     *
     * @param statement
     * @return
     */
    int update(Statement statement);

    /**
     * 获取转为？后的sql
     *
     * @return
     */
    BoundSql getBoundSql();

}
