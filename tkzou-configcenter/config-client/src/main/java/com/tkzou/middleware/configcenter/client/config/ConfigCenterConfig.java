package com.tkzou.middleware.configcenter.client.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 当前配置中心的配置文件
 *
 * @author zoutongkun
 * @date 2022/9/30 11:38
 */
@ConfigurationProperties("spring.cloud.zoutongkun.config")
@Data
@Component
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
