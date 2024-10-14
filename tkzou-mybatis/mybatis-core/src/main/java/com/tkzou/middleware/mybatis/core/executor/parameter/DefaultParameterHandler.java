package com.tkzou.middleware.mybatis.core.executor.parameter;

import cn.hutool.core.util.ReflectUtil;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.type.TypeHandler;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
 * <p> 默认参数处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 21:26
 */
public class DefaultParameterHandler implements ParameterHandler {

    private Configuration configuration;

    public DefaultParameterHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 设置参数
     * 即：把类似如下sql的user对象中的具体值设置到sql中的user.name中！
     *
     * @param ps
     * @param parameter         此时是一个map，key为参数名，value为对应的值，可能是基础类型，也可能是对象！
     * @param parameterMappings 即#{}中的值，如id，当为insert时则是user.name格式
     * @Insert("insert into t_user(name, age) values(#{user.name}, #{user.age})")
     * Long insert(@Param("user") User user);
     */
    @SneakyThrows
    @Override
    public void setParam(PreparedStatement ps, Object parameter, List<String> parameterMappings) {
        // 设置值
        Map<Class<?>, TypeHandler<?>> typeHandlerMap = this.configuration.getTypeHandlerMap();
        //是个map，强转一下
        Map<String, Object> paramValueMap = (Map<String, Object>) parameter;
        //一个一个参数赋值
        for (int i = 0; i < parameterMappings.size(); i++) {
            //参数名称
            String jdbcColumnName = parameterMappings.get(i);
            //1.处理insert，因为它是需要从对象中解析，如user.name特别一些
            if (jdbcColumnName.contains(".")) {
                String[] split = jdbcColumnName.split("\\.");
                //1.1对象名，如user
                String key = split[0];
                //1.2获取该参数名称对应的值，是个对象
                Object instanceValue = paramValueMap.get(key);
                //1.3该对象中的具体的属性，如name
                String fieldName = split[1];
                //1.4通过反射即可获取该字段的值！
                Object fieldValue = ReflectUtil.getFieldValue(instanceValue, fieldName);
                //1.5再设置到sql中
                //setParameter方法中的下标是从1开始的，而不是从0开始！！！
                //按类型使用对应的类型处理器设置
                TypeHandler typeHandler = typeHandlerMap.get(fieldValue.getClass());
                typeHandler.setParameter(ps, i + 1, fieldValue);
            } else {
                //2.处理删改查，此时直接按照参数名称即可获取具体的值，再设置到sql中即可！
                Object val = paramValueMap.get(jdbcColumnName);
                //同理，按类型使用对应的类型处理器设置
                TypeHandler typeHandler = typeHandlerMap.get(val.getClass());
                typeHandler.setParameter(ps, i + 1, val);
            }
        }
    }
}
