package com.tkzou.middleware.dynamicdb.config;

import com.tkzou.middleware.dynamicdb.core.DynamicDataSource;
import com.tkzou.middleware.dynamicdb.meta.DataSourceMeta;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 加载数据库中的数据源信息到ioc中
 *
 * @author zoutongkun
 */
@Component
public class LoadDataSourceRunner implements CommandLineRunner {
    @Resource
    private DynamicDataSource dynamicDataSource;

    @Override
    public void run(String... args) {
        List<DataSourceMeta> dataSourceMetas = getDataSourceConfigFromDb();
        dynamicDataSource.loadDataSource(dataSourceMetas);
    }

    /**
     * 从数据库中读取数据源信息
     *
     * @return
     */
    private List<DataSourceMeta> getDataSourceConfigFromDb() {
        //todo 从数据库中读取数据源信息，更方便配置！
        return null;
    }
}