package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.netflix.loadbalancer.Server;
import com.tkzou.middleware.springcloud.loadbalancer.domain.MyRibbonServer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务管理器
 * 就是管理所有注册在注册中心中的服务
 *
 * @author :zoutongkun
 * @date :2024/10/13 13:01
 * @description :
 * @modyified By:
 */
public class ServiceManager {
    /**
     * 所有服务实例信息
     * key:服务名称
     * value：当前服务下的所有服务实例
     */
    public static volatile ConcurrentHashMap<String, List<Server>> SERVICE_INFO_MAP =
        new ConcurrentHashMap<>();
    /**
     * 服务发现
     */
    private final DiscoveryClient discoveryClient;

    public ServiceManager(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        //初始化时从注册中心拉取最新的服务注册信息到本地
        pullServiceInfoFromRegister();
    }

    /**
     * 获取所有服务实例
     */
    public List<Server> getAllServers() {
        List<Server> servers = new ArrayList<>();
        Collection<List<Server>> values = SERVICE_INFO_MAP.values();
        for (List<Server> value : values) {
            servers.addAll(value);
        }
        return servers;
    }

    /**
     * 根据服务名称获取对应的服务实例
     *
     * @param serviceName
     * @return
     */
    public List<Server> getServer(String serviceName) {
        List<Server> servers = SERVICE_INFO_MAP.get(serviceName);
        if (CollectionUtil.isEmpty(servers)) {
            return Collections.emptyList();
        }
        return servers;
    }

    /**
     * 从服务注册中心定时拉取服务注册信息到本地
     */
    private void pullServiceInfoFromRegister() {
        //使用一个定时任务从服务注册中心拉取最新的服务注册信息！！！
        ScheduledThreadPoolExecutor scheduledExecutor = ThreadUtil.createScheduledExecutor(1);
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            //从注册中心拉取最新的服务注册信息
            List<String> services = discoveryClient.getServices();
            services.forEach(serviceName -> {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
                List<Server> servers = new ArrayList<>();
                for (ServiceInstance instance : instances) {
                    Server server = toRibbonService(instance);
                    servers.add(server);
                    SERVICE_INFO_MAP.put(serviceName, servers);
                }
            });
            //每10s拉取一次
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 转为ribbon协议中的server
     *
     * @param instance
     * @return
     */
    private Server toRibbonService(ServiceInstance instance) {
        return new MyRibbonServer(instance.getServiceId(), instance.getHost(), instance.getPort());
    }
}
