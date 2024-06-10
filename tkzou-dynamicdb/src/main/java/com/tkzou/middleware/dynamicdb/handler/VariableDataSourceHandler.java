package com.tkzou.middleware.dynamicdb.handler;

import org.springframework.stereotype.Component;

/**
 * 实际数据源获取器
 * 搭配@DataSource注解一起使用
 *
 * @author zoutongkun
 */
public interface VariableDataSourceHandler {
    /**
     * 根据参数获取实际的数据源
     *
     * @param datasourceType 数据源类型，如主库还是从库
     * @param parameterNames
     * @param parameterTypes
     * @param args
     * @return
     */
    String getRealDataSource(String datasourceType, String[] parameterNames, Class[] parameterTypes, Object[] args);

    /**
     * 默认实现
     */
    @Component
    class Default implements VariableDataSourceHandler {
        public Default() {
        }

        @Override
        public String getRealDataSource(String datasourceType, String[] parameterNames, Class[] parameterTypes,
                                        Object[] args) {
            return datasourceType;
        }
    }
}