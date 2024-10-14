package com.tkzou.middleware.mybatis.core.executor.resultset;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.type.TypeHandler;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

/**
 * <p> 默认结果处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 20:38
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private Configuration configuration;

    public DefaultResultSetHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    @SneakyThrows
    @Override
    public <T> List<T> handleResultSets(MappedStatement ms, PreparedStatement ps) {
        //1.先拿到mapper中该方法定义的返回值的类型的clazz对象
        Class<?> resTypeClazz = ms.getReturnType();
        //2.再拿到原始的结果集
        ResultSet rs = ps.getResultSet();
        //3.拿到sql返回的所有字段名称
        List<String> columnList = Lists.newArrayList();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            columnList.add(metaData.getColumnName(i + 1));
        }

        //4.拿到所有的类型处理器
        Map<Class<?>, TypeHandler<?>> typeHandlerMap = this.configuration.getTypeHandlerMap();
        //都看成list，对于selectone，取get(0）即可
        List list = Lists.newArrayList();
        // 5.结果映射
        // 遍历结果集的每一行，封装成返回对象！
        while (rs.next()) {
            // 5.1根据返回类型创建返回值对象
            // 就是利用的反射
            Object resObj = resTypeClazz.newInstance();
            //5.2处理每一列
            for (String columnName : columnList) {
                //1）获取对应的字段类型（根据class就可以获取，无需对象！）
                Class<?> fieldType = ReflectUtil.getField(resTypeClazz, columnName).getType();
                //2）获取对应的类型处理器
                TypeHandler<?> typeHandler = typeHandlerMap.get(fieldType);
                //3）解析出对应的值
                Object val = typeHandler.getResult(rs, columnName);
                //4）最后设置到结果集对象中！
                ReflectUtil.setFieldValue(resObj, columnName, val);
            }
            //5.3添加进结果集
            list.add(resObj);
        }
        rs.close();
        ps.close();
        //6.返回结果集
        return list;
    }

}
