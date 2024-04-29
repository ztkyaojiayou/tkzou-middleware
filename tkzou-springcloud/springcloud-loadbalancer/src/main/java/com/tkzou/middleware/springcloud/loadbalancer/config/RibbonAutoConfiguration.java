package com.tkzou.middleware.springcloud.loadbalancer.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.tkzou.middleware.springcloud.loadbalancer.annotation.RibbonClients;
import com.tkzou.middleware.springcloud.loadbalancer.core.LoadBalanceClientContextFactory;
import com.tkzou.middleware.springcloud.loadbalancer.core.RibbonLoadBalancerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zoutongkun
 * @date 2024/4/28
 */
@Configuration
@RibbonClients(defaultConfiguration = MyRibbonClientConfiguration.class)
public class RibbonAutoConfiguration {
    /**
     * 各服务定制化的配置类
     */
    @Autowired(required = false)
    private List<RibbonClientSpecification> configurations = new ArrayList<>();

    /**
     * 注册SpringClientFactory
     * 核心类，用于给每个服务生成一个独立的ioc容器
     *
     * @return
     */
    @Bean
    public LoadBalanceClientContextFactory springClientFactory() {
        LoadBalanceClientContextFactory factory = new LoadBalanceClientContextFactory();
        factory.setConfigurations(this.configurations);
        return factory;
    }

    /**
     * 注册LoadBalancerClient
     * 核心类，负载均衡器，用于从多个服务实例中基于负载均衡策略选取一个服务实例
     * 同时重组url并执行！
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(LoadBalancerClient.class)
    public LoadBalancerClient loadBalancerClient() {
        return new RibbonLoadBalancerClient(springClientFactory());
    }

    /**
     * 注册一种默认的负载均衡策略
     * 这里就为ZoneAvoidanceRule
     *
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public IRule ribbonRule(IClientConfig config) {
        ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }

    /**
     * 注册一种默认的负载均衡器
     * 这里注册的就是ZoneAwareLoadBalancer
     *
     * @param config
     * @param serverList
     * @param serverListFilter
     * @param rule
     * @param ping
     * @param serverListUpdater
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
        return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
                serverListFilter, serverListUpdater);
    }
}
