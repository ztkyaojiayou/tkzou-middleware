package com.tkzou.middleware.configcenter.client.config;

import com.tkzou.middleware.configcenter.client.core.ConfigCenterPropertySourceLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配，用来适配SpringCloud配置中心
 * 它在进行spi导出时的key必须是：org.springframework.cloud.bootstrap.BootstrapConfiguration
 * 这是springCloud规范！
 *
 * @author zoutongkun
 * @date 2022/9/30 00:38
 */
@Configuration
public class ConfigCenterBootstrapConfiguration {

    /**
     * 核心类，用于ioc容器初始化前从配置中心加载配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigCenterPropertySourceLocator configCenterPropertySourceLocator() {
        return new ConfigCenterPropertySourceLocator();
    }

}
