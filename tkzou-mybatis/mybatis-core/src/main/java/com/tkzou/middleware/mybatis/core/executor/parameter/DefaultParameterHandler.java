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
     * 即：把类似如下sql的user对象中的值设置到user.name等中！
     *
     * @param ps
     * @param parameter
     * @param parameterMappings
     * @Insert("insert into t_user(name, age) values(#{user.name}, #{user.age})")
     * Long insert(@Param("user") User user);
     */
    @SneakyThrows
    @Override
    public void setParam(PreparedStatement ps, Object parameter, List<String> parameterMappings) {
        // 设置值
        Map<Class, TypeHandler> typeHandlerMap = this.configuration.getTypeHandlerMap();
        Map<String, Object> paramValueMap = (Map<String, Object>) parameter;
        for (int i = 0; i < parameterMappings.size(); i++) {
            String jdbcColumnName = parameterMappings.get(i);
            //1.处理insert，因为它是需要从对象中解析，如user.name特别一些
            if (jdbcColumnName.contains(".")) {
                String[] split = jdbcColumnName.split("\\.");
                String key = split[0];
                Object instanceValue = paramValueMap.get(key);
                Object fieldValue = ReflectUtil.getFieldValue(instanceValue, split[1]);
                //setParameter方法中的下标是从1开始的，而不是从0开始！！！
                typeHandlerMap.get(fieldValue.getClass()).setParameter(ps, i + 1, fieldValue);
            } else {
                //2.处理删改查，此时直接按照参数名称的对应去设置即可！
                Object val = paramValueMap.get(jdbcColumnName);
                typeHandlerMap.get(val.getClass()).setParameter(ps, i + 1, val);
            }
        }
    }
}
