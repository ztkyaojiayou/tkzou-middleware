package com.tkzou.middleware.mybatis.core.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p> 出入参 字段类型处理器 </p>
 * 易知，首先需要保存一下mapper方法中的入参和结果集的类型
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 02:58
 */
public interface TypeHandler<T> {

    /**
     * 根据入参类型设置参数值
     *
     * @param ps        PreparedStatement
     * @param i         参数位置
     * @param parameter 参数值
     * @return void
     * @author zoutongkun
     * @date 2024/4/22 16:19
     */
    void setParameter(PreparedStatement ps, int i, T parameter) throws SQLException;

    /**
     * 根据列类型获取返回值
     *
     * @param rs         ResultSet结果集
     * @param columnName 字段名称
     * @return 字段值
     * @author zoutongkun
     * @date 2024/4/22 16:19
     */
    T getResult(ResultSet rs, String columnName) throws SQLException;

}
