package com.tkzou.middleware.mybatis.core.datasource;

import javax.sql.DataSource;

/**
 * 数据库连接池工厂
 *
 * @author zoutongkun
 */
public interface DataSourceFactory {
    /**
     * 获取一个连接池
     *
     * @return
     */
    DataSource getDataSource();
}