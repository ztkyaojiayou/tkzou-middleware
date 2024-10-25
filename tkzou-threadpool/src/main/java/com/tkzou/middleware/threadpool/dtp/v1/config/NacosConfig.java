package com.tkzou.middleware.threadpool.dtp.v1.config;

import lombok.Data;

/**
 * nacos上的配置文件信息
 *
 * @author zoutongkun
 * @Date 2023/5/22 22:04
 */
@Data
public class NacosConfig {
    /**
     * 配置文件id
     */
    private String dataId;
    /**
     * 配置文件所属组
     */
    private String group;
}
