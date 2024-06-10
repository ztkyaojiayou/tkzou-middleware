package com.tkzou.middleware.dynamicdb.config;

import cn.hutool.core.thread.ThreadUtil;
import com.tkzou.middleware.dynamicdb.core.DynamicDataSource;
import com.tkzou.middleware.dynamicdb.meta.DataSourceMeta;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 加载数据库中的数据源信息到ioc中
 * 使用定时任务拉取更新！
 *
 * @author zoutongkun
 */
@Component
public class LoadDataSourceRunner implements CommandLineRunner {
    private static final ScheduledThreadPoolExecutor scheduledExecutor = ThreadUtil.createScheduledExecutor(1);

    @Resource
    private DynamicDataSource dynamicDataSource;

    @Override
    public void run(String... args) {
        // 定时任务，每隔1小时拉取更新一次
        scheduledExecutor.scheduleAtFixedRate(() -> {
            List<DataSourceMeta> dataSourceMetas = getDataSourceConfigFromDb();
            dynamicDataSource.loadDataSource(dataSourceMetas);
        }, 1L, 1L, TimeUnit.HOURS);
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