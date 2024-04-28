package com.tkzou.middleware.springcloud.loadbalancer.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import com.tkzou.middleware.springcloud.loadbalancer.core.MyServerList;
import com.tkzou.middleware.springcloud.registercenter.client.config.MyDiscoveryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义ribbon组件
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@Configuration
public class MyRibbonClientConfiguration {
    /**
     * 注册ServerList
     * 核心类，用于从远程注册中心获取服务列表
     *
     * @param config
     * @param discoveryProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerList<?> ribbonServerList(IClientConfig config,
                                          MyDiscoveryProperties discoveryProperties) {
        MyServerList serverList = new MyServerList(discoveryProperties);
        serverList.initWithNiwsConfig(config);
        return serverList;
    }
}
