package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * 自定义负载均衡规则
 * 就是随机规则
 *
 * @author :zoutongkun
 * @date :2024/10/13 12:26
 * @description :
 * @modyified By:
 */
public class MyRandomRule extends AbstractLoadBalancerRule {
    /**
     * 服务管理器
     */
    private final ServiceManager serviceManager;

    public MyRandomRule(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    /**
     * 随机选择一个具体的服务实例
     * 核心方法
     *
     * @param serviceName
     * @return
     */
    @Override
    public Server choose(Object serviceName) {
        List<Server> servers = serviceManager.getServer(String.valueOf(serviceName));
        if (CollectionUtil.isEmpty(servers)) {
            return null;
        }
        //随机选择一个具体的服务实例
        int randomIndex = RandomUtil.randomInt(0, servers.size());
        return servers.get(randomIndex);
    }
}
