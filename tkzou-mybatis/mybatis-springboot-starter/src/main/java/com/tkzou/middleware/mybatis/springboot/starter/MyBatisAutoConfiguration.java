package com.tkzou.middleware.mybatis.springboot.starter;

import com.alibaba.druid.pool.DruidDataSource;
import com.tkzou.middleware.mybatis.core.session.SqlSession;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactory;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactoryBuilder;
import com.tkzou.middleware.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * <p> mybatis自动配置类 </p>
 * 也是核心配置类！
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 04:07
 */
@EnableTransactionManagement // 开启事务
@EnableConfigurationProperties(MyBatisConfigProperty.class)
public class MyBatisAutoConfiguration {

    @Autowired
    private MyBatisConfigProperty myBatisConfigProperty;

    /**
     * 配置sqlSession
     *
     * @param dataSource
     * @return
     */
    @Bean
    public SqlSession sqlSession(DataSource dataSource) {
        String mapperName = this.myBatisConfigProperty.getMapper();
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(dataSource, new SpringManagedTransaction(dataSource), mapperName);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }

    /**
     * 创建DruidDataSource数据库连接池
     * 这里没有使用自己写的了，毕竟不够完善....
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis-tkzou?useUnicode=true&characterEncoding=UTF8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    /**
     * 创建 JdbcTemplate 对象
     * 测试使用
     *
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    /**
     * 创建spring的事务管理器
     * 也没有使用自己写的了，毕竟不够完善....
     *
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
