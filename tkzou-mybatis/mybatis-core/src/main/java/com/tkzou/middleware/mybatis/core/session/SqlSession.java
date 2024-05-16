package com.tkzou.middleware.mybatis.core.session;

import java.util.List;

/**
 * <p> 操作增删改查核心接口 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 01:21
 */
public interface SqlSession {

    int insert(String statementId, Object parameter);

    int delete(String statementId, Object parameter);

    int update(String statementId, Object parameter);

    <T> T selectOne(String statementId, Object parameter);

    <T> List<T> selectList(String statementId, Object parameter);

    <T> T getMapper(Class<T> mapper);

    Configuration getConfiguration();

    void commit();

    void rollback();

    void close();

}
