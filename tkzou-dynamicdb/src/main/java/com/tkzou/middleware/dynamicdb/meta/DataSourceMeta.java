package com.tkzou.middleware.dynamicdb.meta;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据源元数据
 *
 * @author zoutongkun
 */
@Data
@Accessors(chain = true)
public class DataSourceMeta {

    /**
     * 数据库地址
     */
    private String url;
    /**
     * 数据库用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 数据库驱动
     */
    private String driverClassName;
    /**
     * 数据库key，即保存Map中的key
     */
    private String key;
}