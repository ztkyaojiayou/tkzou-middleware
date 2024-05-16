package com.tkzou.middleware.mybatis.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.tkzou.middleware.mybatis.core.session.SqlSession;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactory;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactoryBuilder;
import com.tkzou.middleware.mybatis.spring.annotation.MapperScan;
import com.tkzou.middleware.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * <p> mybatis配置 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 04:07
 */
@EnableTransactionManagement // 开启事务
@ComponentScan("com.tkzou.middleware.mybatis.spring.test")
@MapperScan("com.tkzou.middleware.mybatis.spring.test.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSession sqlSession(DataSource dataSource) {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(dataSource, new SpringManagedTransaction(dataSource), "com.zhengqing.mybatis.demo.mapper");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }

    // 创建数据库连接池
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis-tkzou?useUnicode=true&characterEncoding=UTF8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    // 创建 JdbcTemplate 对象
    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    // 创建事务管理器
    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
