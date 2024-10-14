package com.tkzou.middleware.mybatis.core.executor.parameter;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * <p> 参数处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 21:26
 */
public interface ParameterHandler {
    /**
     * 设置sql参数
     * 即把原始sql中的#{user.name}替换为实际传入的参数
     *
     * @param ps
     * @param parameter
     * @param parameterMappings
     */
    void setParam(PreparedStatement ps, Object parameter, List<String> parameterMappings);

}
