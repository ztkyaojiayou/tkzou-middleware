package com.tkzou.middleware.mybatis.core.session;

import com.tkzou.middleware.mybatis.core.builder.MapperAnnotationBuilder;
import com.tkzou.middleware.mybatis.core.session.defaults.DefaultSqlSessionFactory;
import com.tkzou.middleware.mybatis.core.transaction.Transaction;

import javax.sql.DataSource;

/**
 * <p> SqlSession工厂构建者 </p>
 * 进一步封装对SqlSessionFactory的创建，牛逼！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 02:09
 */
public class SqlSessionFactoryBuilder {
    /**
     * 构建SqlSessionFactory，同时封装对XMLConfigBuilder和Configuration的初始化！
     *
     * @return
     */
    public SqlSessionFactory build() {
        MapperAnnotationBuilder mapperAnnotationBuilder = new MapperAnnotationBuilder();
        Configuration configuration = mapperAnnotationBuilder.parse();
        return new DefaultSqlSessionFactory(configuration);
    }

    public SqlSessionFactory build(DataSource dataSource, Transaction transaction) {
        MapperAnnotationBuilder mapperAnnotationBuilder = new MapperAnnotationBuilder();
        Configuration configuration = mapperAnnotationBuilder.parse(dataSource, transaction, null);
        return new DefaultSqlSessionFactory(configuration);
    }

    public SqlSessionFactory build(DataSource dataSource, Transaction transaction, String mapperPackageName) {
        MapperAnnotationBuilder mapperAnnotationBuilder = new MapperAnnotationBuilder();
        Configuration configuration = mapperAnnotationBuilder.parse(dataSource, transaction, mapperPackageName);
        return new DefaultSqlSessionFactory(configuration);
    }


}
