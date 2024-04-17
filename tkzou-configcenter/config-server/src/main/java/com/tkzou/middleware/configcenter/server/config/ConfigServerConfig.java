package com.tkzou.middleware.configcenter.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置文件相关配置项
 *
 * @author zoutongkun
 */
@ConfigurationProperties("tkzou.config")
@Component
@Getter
@Setter
public class ConfigServerConfig {

    /**
     * 存储类型 ，memory:基于内存存储 file:文件系统存储（默认）
     */
    private String storeType = "file";

}
