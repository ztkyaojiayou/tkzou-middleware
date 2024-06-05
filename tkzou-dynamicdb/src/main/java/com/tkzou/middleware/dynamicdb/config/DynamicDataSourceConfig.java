package com.tkzou.middleware.dynamicdb.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.tkzou.middleware.dynamicdb.constant.DbConstant;
import com.tkzou.middleware.dynamicdb.core.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

    @Bean(name = DbConstant.MYSQL_MASTER)
    @ConfigurationProperties("spring.datasource.mysql.master")
    public DataSource masterDataSource() {
        log.info("数据源切换为：{}", DbConstant.MYSQL_MASTER);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbConstant.MYSQL_SLAVE)
    @ConfigurationProperties("spring.datasource.mysql.slave")
    public DataSource slaveDataSource() {
        log.info("数据源切换为：{}", DbConstant.MYSQL_SLAVE);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbConstant.ORACLE_MASTER)
    @ConfigurationProperties("spring.datasource.oracle.master")
    public DataSource oracleMasterDataSource() {
        log.info("数据源切换为oracle：{}", DbConstant.ORACLE_MASTER);
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DbConstant.ORACLE_SLAVE)
    @ConfigurationProperties("spring.datasource.oracle.slave")
    public DataSource oracleSlaveDataSource() {
        log.info("数据源切换为oracle：{}", DbConstant.ORACLE_SLAVE);
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    /**
     * 注册所有数据源
     *
     * @return
     */
    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        //使用map组装所有候选数据源
        Map<Object, Object> dataSourceMap = new HashMap<>(3);
        dataSourceMap.put(DbConstant.MYSQL_MASTER, masterDataSource());
        dataSourceMap.put(DbConstant.MYSQL_SLAVE, slaveDataSource());
        dataSourceMap.put(DbConstant.ORACLE_MASTER, oracleMasterDataSource());
        dataSourceMap.put(DbConstant.ORACLE_SLAVE, oracleSlaveDataSource());
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

//    /**
//     * 手动配置SqlSessionFactory，以便于它使用动态数据源。
//     * todo 有些版本不需要
//     * @param dynamicDataSource
//     * @return
//     * @throws Exception
//     */
//    @Bean("sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactoryBean(DynamicDataSource dynamicDataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
//        return sqlSessionFactoryBean.getObject();
//    }
//
//    /**
//     * 重写事务管理器，管理动态数据源
//     * todo 有些版本不需要
//     */
//    @Primary
//    @Bean(value = "transactionManager")
//    public PlatformTransactionManager annotationDrivenTransactionManager(DynamicDataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }

}
