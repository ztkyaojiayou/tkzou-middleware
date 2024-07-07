package com.tkzou.middleware.dynamicdb.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.tkzou.middleware.dynamicdb.constant.DbTypeConstant;
import com.tkzou.middleware.dynamicdb.core.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO： 数据源信息配置类，读取数据源配置信息并注册成bean。
 * @Author: zoutongkun
 * @CreateDate: 2024/5/16 14:54
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig {

    @Bean(name = DbTypeConstant.MYSQL_MASTER)
    @ConfigurationProperties("spring.datasource.mysql.master")
    public DataSource masterDataSource() {
        log.info("数据源切换为：{}", DbTypeConstant.MYSQL_MASTER);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbTypeConstant.MYSQL_SLAVE)
    @ConfigurationProperties("spring.datasource.mysql.slave")
    public DataSource slaveDataSource() {
        log.info("数据源切换为：{}", DbTypeConstant.MYSQL_SLAVE);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbTypeConstant.ORACLE_MASTER)
    @ConfigurationProperties("spring.datasource.oracle.master")
    public DataSource oracleMasterDataSource() {
        log.info("数据源切换为oracle：{}", DbTypeConstant.ORACLE_MASTER);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbTypeConstant.ORACLE_SLAVE)
    @ConfigurationProperties("spring.datasource.oracle.slave")
    public DataSource oracleSlaveDataSource() {
        log.info("数据源切换为oracle：{}", DbTypeConstant.ORACLE_SLAVE);
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    /**
     * 初始化一个动态数据源，
     * 用于注册所有数据源，
     * 同时提供了获取当前线程的数据源的方法
     *
     * @return
     */
    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        //使用map组装所有候选数据源
        Map<Object, Object> dataSourceMap = new HashMap<>(3);
        dataSourceMap.put(DbTypeConstant.MYSQL_MASTER, masterDataSource());
        dataSourceMap.put(DbTypeConstant.MYSQL_SLAVE, slaveDataSource());
        dataSourceMap.put(DbTypeConstant.ORACLE_MASTER, oracleMasterDataSource());
        dataSourceMap.put(DbTypeConstant.ORACLE_SLAVE, oracleSlaveDataSource());
        //设置动态数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //设置默认数据源（非必须）
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
        //注册所有数据源（必须！）
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        //将数据源信息备份在defineTargetDataSources中备用（非必须）
        dynamicDataSource.setDefineTargetDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    /**
     * 手动配置SqlSessionFactory，以便于它使用动态数据源。
     * todo 有些版本不需要
     *
     * @param dynamicDataSource
     * @return
     * @throws Exception
     */
    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 重写事务管理器，管理动态数据源
     * 此时注入的这个数据源就是一个动态数据源！
     * 事务管理器是和数据源有关的，一个事务管理器需要绑定一个数据源，
     * 这里绑定的就是动态数据源，也即即使当前配置了很多数据源，
     * 但在某一个方法中依然只在操作一个数据源！！！
     * 否则就是分布式事务了！！！
     * todo 有些版本不需要
     */
    @Primary
    @Bean(value = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager(DynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
