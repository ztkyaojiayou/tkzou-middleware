package com.tkzou.middleware.mybatis.core.session.defaults;

import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.session.SqlSession;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactory;
import com.tkzou.middleware.mybatis.core.transaction.Transaction;

/**
 * <p> 默认的SqlSessionFactory </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 01:58
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return this.openSession(true);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        //获取一个带事务的连接
        Transaction transaction = this.configuration.getTransaction(autoCommit);
        //使用了默认的DefaultSqlSession
        return new DefaultSqlSession(this.configuration, this.configuration.newExecutor(transaction));
    }
}
