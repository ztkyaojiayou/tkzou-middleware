package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.core.util.ObjectUtil;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * 负载均衡器
 *
 * @author :zoutongkun
 * @date :2024/10/13 12:20
 * @description :
 * @modyified By:
 */
public class MyLoadBalancer implements ILoadBalancer {
    /**
     * 实际的负载均衡策略
     * 这里就是注入自定义的MyRandomRule
     */
    private final IRule rule;
    /**
     * 服务管理器
     */
    private final ServiceManager serviceManager;

    public MyLoadBalancer(IRule rule, ServiceManager serviceManager) {
        this.rule = rule;
        this.serviceManager = serviceManager;
    }

    /**
     * 获取所有服务实例
     * 具体一般就是从注册中心获取！！！
     */
    @Override
    public List<Server> getAllServers() {
        return serviceManager.getAllServers();
    }

    /**
     * 选择一个服务实例
     *
     * @param serviceName
     * @return
     */
    @Override
    public Server chooseServer(Object serviceName) {
        if (ObjectUtil.isEmpty(serviceName)) {
            return null;
        }
        return rule.choose(serviceName);
    }

    @Override
    public List<Server> getServerList(boolean b) {
        return null;
    }

    @Override
    public void addServers(List<Server> list) {

    }

    @Override
    public List<Server> getReachableServers() {
        return null;
    }

    @Override
    public void markServerDown(Server server) {
    }

}
