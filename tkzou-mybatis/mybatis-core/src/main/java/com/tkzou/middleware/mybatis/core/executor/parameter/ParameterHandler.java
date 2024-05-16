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

    void setParam(PreparedStatement ps, Object parameter, List<String> parameterMappings);

}
