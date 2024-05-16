package com.tkzou.middleware.mybatis.core.executor.resultset;

import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * <p> 结果处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 20:37
 */
public interface ResultSetHandler {

    <T> List<T> handleResultSets(MappedStatement ms, PreparedStatement ps);

}
