package com.tkzou.middleware.mybatis.core.datasource;

import javax.sql.DataSource;

/**
 * 数据库连接池生产工厂
 *
 * @author zoutongkun
 */
public class PooledDataSourceFactory implements DataSourceFactory {
    /**
     * 创建一个连接池
     *
     * @return
     */
    @Override
    public DataSource getDataSource() {
        return create();
    }

    /**
     * 使用static方式创建一个连接池
     *
     * @return
     */
    public static DataSource create() {
        return new PooledDataSource();
    }

}
