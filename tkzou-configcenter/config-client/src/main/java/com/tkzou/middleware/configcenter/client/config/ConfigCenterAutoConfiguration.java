package com.tkzou.middleware.configcenter.client.config;

import com.tkzou.middleware.configcenter.client.core.ConfigService;
import com.tkzou.middleware.configcenter.client.listener.ConfigContextRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配，即把需要的bean统一装配到ioc容器
 *
 * @author zoutongkun
 * @date 2022/9/30 00:38
 */
@Configuration
public class ConfigCenterAutoConfiguration {

    /**
     * 当前配置中心的配置文件，即对应的web端地址
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigCenterConfig configCenterConfig() {
        return new ConfigCenterConfig();
    }

    /**
     * 配置服务核心类
     * 注意功能：实例化configClient，刷新最新配置到本地缓存
     * @param configCenterConfig
     * @return
     */
    @Bean
    public ConfigService configService(ConfigCenterConfig configCenterConfig) {
        //这个需要一个web端的ip地址，目的就是获取与web端交互的ConfigClient对象，如获取配置文件的最新数据
        return new ConfigService(configCenterConfig.getServerAddr());
    }

    /**
     * 配置刷新器
     *
     * @return
     */
    @Bean
    public ConfigContextRefresher configContextRefresher(ConfigCenterConfig configCenterConfig, ConfigService configService) {
        return new ConfigContextRefresher(configCenterConfig, configService);
    }

}
