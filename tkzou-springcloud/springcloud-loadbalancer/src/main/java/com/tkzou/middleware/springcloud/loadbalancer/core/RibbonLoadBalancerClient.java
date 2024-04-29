package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.core.util.StrUtil;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.tkzou.middleware.springcloud.registercenter.client.discovery.MyServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.Request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * ribbon负载均衡客户端
 * 核心，是在RibbonAutoConfiguration配置类中自动注入的
 * 作用：负载均衡器，用于从多个服务实例中基于负载均衡策略选取一个服务实例，同时重组url并执行！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class RibbonLoadBalancerClient implements LoadBalancerClient {

    private LoadBalanceClientContextFactory clientFactory;

    public RibbonLoadBalancerClient(LoadBalanceClientContextFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * 选择服务实例
     *
     * @param serviceId
     * @return
     */
    @Override
    public ServiceInstance choose(String serviceId) {
        return choose(serviceId, null);
    }

    /**
     * 选择服务实例
     *
     * @param serviceId
     * @param request
     * @param <T>
     * @return
     */
    @Override
    public <T> ServiceInstance choose(String serviceId, Request<T> request) {
        //从当前服务对应的ioc容器中获取一个负载均衡器
        ILoadBalancer loadBalancer = clientFactory.getInstance(serviceId, ILoadBalancer.class);
        //再基于负载均衡策略选择一个具体的服务实例
        //在配置类中自动注入了ZoneAwareLoadBalancer这个负载均衡策略！
        Server server = loadBalancer.chooseServer("default");
        if (server != null) {
            //再转为springcloud标准化的服务实例对象--ServiceInstance
            return new MyServiceInstance(serviceId, server.getHost(), server.getPort());
        }
        return null;
    }

    /**
     * 重建请求URI，将服务名称替换为服务实例的IP:端口
     *
     * @param server
     * @param original
     * @return
     */
    @Override
    public URI reconstructURI(ServiceInstance server, URI original) {
        try {
            //将服务名称替换为服务实例的IP:端口，例如http://provider-application/echo被重建为http://192.168.100.1:8888/echo
            StringBuilder sb = new StringBuilder();
            sb.append(original.getScheme()).append("://");
            sb.append(server.getHost());
            sb.append(":").append(server.getPort());
            sb.append(original.getRawPath());
            if (StrUtil.isNotEmpty(original.getRawQuery())) {
                sb.append("?").append(original.getRawQuery());
            }
            URI newURI = new URI(sb.toString());
            return newURI;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理http请求
     * 核心
     *
     * @param serviceId
     * @param request
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
        ServiceInstance serviceInstance = choose(serviceId);
        return execute(serviceId, serviceInstance, request);
    }

    /**
     * 处理http请求
     * 最终会由openfeign提供调用url的httpClient
     *
     * @param serviceId
     * @param serviceInstance
     * @param request
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException {
        try {
            return request.apply(serviceInstance);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
