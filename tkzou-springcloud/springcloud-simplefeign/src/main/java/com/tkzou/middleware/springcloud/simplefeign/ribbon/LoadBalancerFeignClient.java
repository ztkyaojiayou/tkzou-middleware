package com.tkzou.middleware.springcloud.simplefeign.ribbon;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 具备负载均衡能力的feign client
 * 因为集成了ribbon提供的LoadBalancerClient
 * 使用了装饰器模式
 * 装饰了Client
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
public class LoadBalancerFeignClient implements Client {
    /**
     * 由负载均衡模块提供，具体为：RibbonLoadBalancerClient
     */
    private LoadBalancerClient loadBalancerClient;
    /**
     * 实际发送请求的httpClient
     */
    private Client delegate;

    public LoadBalancerFeignClient(LoadBalancerClient loadBalancerClient, Client delegate) {
        this.loadBalancerClient = loadBalancerClient;
        this.delegate = delegate;
    }

    /**
     * 执行请求，最终是通过ribbon中的Client.Default执行的
     * 属于装饰模式！！！
     *
     * @param request
     * @param options
     * @return
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            //客户端负载均衡
            URI original = URI.create(request.url());
            String serviceId = original.getHost();
            //通过ribbon的负载均衡策略选择服务实例
            //因此这里就要引入ribbon的依赖啦！也就是自己写的RibbonLoadBalancerClient
            ServiceInstance serviceInstance = loadBalancerClient.choose(serviceId);
            //重建请求URI，即把服务名改成具体实例的ip+端口
            URI uri = loadBalancerClient.reconstructURI(serviceInstance, original);
            //构建请求
            Request newRequest = Request.create(request.httpMethod(), uri.toASCIIString(), new HashMap<>(),
                    request.body(), StandardCharsets.UTF_8);
            // 再发送请求，此时就需要一个httpClient，就需要在自动配置类中注入，
            // 这里注入的就是feign自带的Client.Default
            return delegate.execute(newRequest, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
