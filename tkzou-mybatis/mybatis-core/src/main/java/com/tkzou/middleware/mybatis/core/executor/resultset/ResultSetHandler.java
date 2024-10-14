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
    /**
     * 处理结果集
     * 就是将原始结果集映射为mapper方法中定义的返回类型！
     *
     * @param ms  当前mapper方法的原材料
     * @param ps  原始结果集
     * @param <T>
     * @return
     */
    <T> List<T> handleResultSets(MappedStatement ms, PreparedStatement ps);

}
