package com.tkzou.middleware.mybatis.core.executor;

import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;

import java.util.List;

/**
 * <p> SQL执行器 </p>
 * 实现普通/简单处理和批处理两种方式
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 23:07
 */
public interface Executor {
    /**
     * 查询
     * 属于泛型方法！
     * 表示：返回任意类型的list
     *
     * @param ms
     * @param parameter 查询参数，一般就是map，但为什么不直接定义为map呢？方便按策略拓展
     * @param <T>
     * @return
     */
    <T> List<T> query(MappedStatement ms, Object parameter);

    /**
     * 增删改
     *
     * @param ms
     * @param parameter
     * @return
     */
    int update(MappedStatement ms, Object parameter);

    void commit();

    void rollback();

    void close();

}
