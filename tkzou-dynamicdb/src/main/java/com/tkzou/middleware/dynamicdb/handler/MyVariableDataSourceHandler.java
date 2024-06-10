package com.tkzou.middleware.dynamicdb.handler;

import org.springframework.stereotype.Component;

/**
 * 数据源切换处理器
 *
 * @author zoutongkun
 * @date 2024-06-10 15:13
 */
@Component
public class MyVariableDataSourceHandler implements VariableDataSourceHandler {
    /**
     * 获取实际的数据源，因为根据酒店id进行了分库，
     * 因此需要根据当前的hotelId来动态获取对应的数据源
     * 比如：HOTEL01_MASER_PROD，表示获取生产环境的hotelId为01的master库数据源
     * 注意：所有的数据源需要事先准备好！
     * 具体在LoadDataSourceRunner中实现
     *
     * @param datasourceType
     * @param parameterNames
     * @param parameterTypes
     * @param args
     * @return
     */
    @Override
    public String getRealDataSource(String datasourceType, String[] parameterNames, Class[] parameterTypes,
                                    Object[] args) {
        //实际的数据源名称构成：当前用户所属的酒店id+要访问的数据库类型+当前项目的环境类型
        return BusinessApiContext.getInstance().getCurrentHotelId().toUpperCase() + "_" + datasourceType + "_" + BusinessApiContext.getInstance().getCurrentEnvironment();
    }
}
