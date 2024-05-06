package com.tkzou.middleware.dbpool.core;

import java.sql.Connection;

/**
 * 数据库连接池API
 * 其实也可以直接使用JDBC规范中的API
 * 但这里因为只实现几个核心方法，因此就没有必要使用JDBC的api了
 *
 * @author zoutongkun
 */
public interface DbPoolService {

    /**
     * 判断连接是否可用，可用返回true
     *
     * @param connection
     * @return
     */
    boolean isAvailable(Connection connection);

    /**
     * 使用重复利用机制获取连接
     *
     * @return
     */
    Connection getConnection();

    /**
     * 使用可回收机制释放连接
     *
     * @param connection
     */
    void release(Connection connection);
}