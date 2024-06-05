package com.tkzou.middleware.dynamicdb.core;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 抽象类AbstractRoutingDataSource，具体的实现类为：DynamicDataSource
 * 实现动态数据源切换，这个类其实可以使用现成的！
 * 参考：org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
 * 参考
 *
 * @Author: zoutongkun
 * @CreateDate: 2024/5/16 14:40
 */
@Data
public abstract class AbstractRoutingDataSource extends AbstractDataSource
        implements InitializingBean {
    /**
     * 目标数据源map集合，存储将要切换的多数据源bean信息
     */
    private Map<Object, Object> targetDataSources;
    /**
     * 未指定数据源时的默认数据源对象
     */
    @Nullable
    private Object defaultTargetDataSource;
    private boolean lenientFallback = true;
    /**
     * 数据源查找接口，通过该接口的getDataSource(String dataSourceName)获取数据源信息
     */
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    /**
     * 解析targetDataSources之后的DataSource的map集合
     */
    @Nullable
    private Map<Object, DataSource> resolvedDataSources;
    @Nullable
    private DataSource resolvedDefaultDataSource;

    /**
     * 初始化数据源到ioc容器中，此时targetDataSources字段已经初始化完成啦!
     * 将targetDataSources的内容转化一下放到resolvedDataSources中，
     * 将defaultTargetDataSource转为DataSource赋值给resolvedDefaultDataSource
     */
    @Override
    public void afterPropertiesSet() {
        //如果目标数据源为空，会抛出异常，在系统配置时应至少传入一个数据源
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        } else {
            //初始化resolvedDataSources的大小
            this.resolvedDataSources = CollectionUtils.newHashMap(this.targetDataSources.size());
            //遍历目标数据源信息map集合，对其中的key，value进行解析
            this.targetDataSources.forEach((key, value) -> {
                //resolveSpecifiedLookupKey方法没有做任何处理，只是将key继续返回
                Object lookupKey = this.resolveSpecifiedLookupKey(key);
                //将目标数据源map集合中的value值（德鲁伊数据源信息）转为DataSource类型
                DataSource dataSource = this.resolveSpecifiedDataSource(value);
                //将解析之后的key，value放入resolvedDataSources集合中
                this.resolvedDataSources.put(lookupKey, dataSource);
            });
            if (this.defaultTargetDataSource != null) {
                //将默认目标数据源信息解析并赋值给resolvedDefaultDataSource
                this.resolvedDefaultDataSource = this.resolveSpecifiedDataSource(this.defaultTargetDataSource);
            }

        }
    }

    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource) dataSource;
        } else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) dataSource);
        } else {
            throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
        }
    }

    /**
     * 获取数据库连接，核心方法
     * 因为AbstractRoutingDataSource继承AbstractDataSource，
     * 而AbstractDataSource实现了DataSource接口，所有存在获取数据源连接的方法
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    /**
     * 寻找当前线程要使用的数据源
     *
     * @return
     */
    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        //调用实现类中重写的determineCurrentLookupKey方法拿到当前线程要使用的数据源的名称
        //核心方法
        Object tarDataSourceKey = this.determineCurrentLookupKey();
        //去解析之后的数据源信息集合中查询该数据源是否存在，如果没有拿到则使用默认数据源resolvedDefaultDataSource
        DataSource dataSource = this.resolvedDataSources.get(tarDataSourceKey);
        if (dataSource == null && (this.lenientFallback || tarDataSourceKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }

        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + tarDataSourceKey + "]");
        } else {
            return dataSource;
        }
    }

    /**
     * 获取当前线程要使用的数据源的名称
     * 核心方法，就是去DynamicDataSourceHolder中获取！
     *
     * @return
     */
    @Nullable
    protected abstract Object determineCurrentLookupKey();
}
