package com.tkzou.middleware.configcenter.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 当前配置中心的配置文件
 * 用于配置配置中心的基本信息！
 * 需要由使用者进行配置！
 *
 * @author zoutongkun
 * @date 2022/9/30 11:38
 */
@ConfigurationProperties("spring.cloud.tkzou.config.client")
@Data
public class ConfigCenterConfig {

    /**
     * 配置中心的地址
     * 也即：ip+port
     */
    private String serverAddr;

    /**
     * 配置文件的id，实际项目中可能有多个，我们这里就只定义了一个
     */
    private String configFileId;

}
