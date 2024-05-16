package com.tkzou.middleware.mybatis.core.transaction;

import java.sql.Connection;

/**
 * <p> 事务管理 </p>
 * 重点就是是否自动提交
 * 因为默认是每条语句都是一个事务，且默认就是true
 * 因此我们需要将事务相关的代码封装起来，由我们自己控制！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/27 21:59
 */
public interface Transaction {
    /**
     * 获取数据库连接
     *
     * @return
     */
    Connection getConnection();

    /**
     * 手动提交事务
     */
    void commit();

    /**
     * 手动回滚事务
     */
    void rollback();

    /**
     * 手动关闭数据库连接
     */
    void close();

}
