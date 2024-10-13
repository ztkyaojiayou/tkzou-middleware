package com.tkzou.middleware.springcloud.loadbalancer.config;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.tkzou.middleware.springcloud.loadbalancer.core.MyLoadBalancer;
import com.tkzou.middleware.springcloud.loadbalancer.core.MyRandomRule;
import com.tkzou.middleware.springcloud.loadbalancer.core.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置ribbon默认组件
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@Configuration
public class RibbonClientConfiguration {

    @Value("${ribbon.client.name}")
    private String name;

    @Bean
    @ConditionalOnMissingBean
    public IClientConfig ribbonClientConfig() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
        config.loadProperties(name);
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceManager serviceManager(DiscoveryClient discoveryClient) {
        return new ServiceManager(discoveryClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public IRule rule(ServiceManager serviceManager) {
        return new MyRandomRule(serviceManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer loadBalancer(IRule rule, ServiceManager serviceManager) {
        return new MyLoadBalancer(rule, serviceManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public IPing ribbonPing(IClientConfig config) {
        return new DummyPing();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerList<Server> ribbonServerList(IClientConfig config) {
        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);
        return serverList;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerListUpdater ribbonServerListUpdater(IClientConfig config) {
        return new PollingServerListUpdater(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerListFilter<Server> ribbonServerListFilter(IClientConfig config) {
        ServerListSubsetFilter filter = new ServerListSubsetFilter();
        filter.initWithNiwsConfig(config);
        return filter;
    }
}






























