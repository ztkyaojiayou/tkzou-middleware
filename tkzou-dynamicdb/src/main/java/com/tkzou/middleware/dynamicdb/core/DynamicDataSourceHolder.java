package com.tkzou.middleware.dynamicdb.core;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.tkzou.middleware.dynamicdb.constant.DbTypeConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源切换处理
 * 本质就是个ThreadLocal
 * DynamicDataSourceHolder类主要是设置当前线程的数据源名称，
 * 移除数据源名称，以及获取当前数据源的名称，便于动态切换
 *
 * @Author: zoutongkun
 * @CreateDate: 2024/5/16 14:51
 */
@Slf4j
public class DynamicDataSourceHolder {
    /**
     * 保存当前线程的动态数据源名称
     */
    private static final TransmittableThreadLocal<String> DYNAMIC_DATASOURCE_KEY = new TransmittableThreadLocal<>();

    /**
     * 设置/切换数据源，决定当前线程使用哪个数据源
     */
    public static void setKey(String key) {
        log.info("数据源切换为：{}", key);
        DYNAMIC_DATASOURCE_KEY.set(key);
    }

    /**
     * 获取动态数据源名称，默认使用mater数据源
     */
    public static String getKey() {
        String key = DYNAMIC_DATASOURCE_KEY.get();
        return key == null ? DbTypeConstant.MYSQL_MASTER : key;
    }

    /**
     * 移除当前数据源
     */
    public static void clear() {
        log.info("移除数据源：{}", DYNAMIC_DATASOURCE_KEY.get());
        DYNAMIC_DATASOURCE_KEY.remove();
    }

}
