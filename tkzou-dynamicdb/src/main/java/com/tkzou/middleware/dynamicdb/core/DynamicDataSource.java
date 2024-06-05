package com.tkzou.middleware.dynamicdb.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tkzou.middleware.dynamicdb.meta.DataSourceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 动态数据源核心类
 *
 * @Description:
 * @Author: zoutongkun
 * @CreateDate: 2024/5/16 14:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    //备份所有数据源信息
    private Map<Object, Object> defineTargetDataSources;
    private Map<Object, Object> targetDataSourceMap;

    /**
     * 决定当前线程使用哪个数据源
     * 核心方法，此时就会去DynamicDataSourceHolder获取当前线程使用的数据源key，
     * 然后根据这个key去获取对应的数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getKey();
    }

    /**
     * 自定义添加数据源信息
     * 比如可以从数据库中读取数据源信息，然后调用此方法添加到ioc容器中
     * 这样更灵活！
     *
     * @param dataSources 数据源实体集合
     * @return 返回添加结果
     */
    public boolean loadDataSource(List<DataSourceMeta> dataSources) {
        try {
            if (CollectionUtils.isNotEmpty(dataSources)) {
                for (DataSourceMeta ds : dataSources) {
                    DruidDataSource dataSource = createDruidDataSource(ds);
                    this.targetDataSourceMap.put(ds.getKey(), dataSource);
                }
                super.setTargetDataSources(this.targetDataSourceMap);
                // 将TargetDataSources中的连接信息放入resolvedDataSources管理
                super.afterPropertiesSet();
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("---程序报错---:{}", e.getMessage());
        }
        return Boolean.FALSE;
    }

    /**
     * 创建数据源
     *
     * @param ds
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @NotNull
    private DruidDataSource createDruidDataSource(DataSourceMeta ds) throws ClassNotFoundException, SQLException {
        //校验数据库是否可以连接
        Class.forName(ds.getDriverClassName());
        DriverManager.getConnection(ds.getUrl(), ds.getUserName(), ds.getPassWord());
        //封装数据源
        DruidDataSource dataSource = new DruidDataSource();
        BeanUtils.copyProperties(ds, dataSource);
        //申请连接时执行validationQuery检测连接是否有效，这里建议配置为TRUE，防止取到的连接不可用
        dataSource.setTestOnBorrow(true);
        //建议配置为true，不影响性能，并且保证安全性。
        //申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);
        //用来检测连接是否有效的sql，要求是一个查询语句。
        dataSource.setValidationQuery("select 1 ");
        dataSource.init();
        return dataSource;
    }

    /**
     * 校验数据源是否存在
     *
     * @param key 数据源保存的key
     * @return 返回结果，true：存在，false：不存在
     */
    public boolean existsDataSource(String key) {
        return Objects.nonNull(this.targetDataSourceMap.get(key));
    }
}
