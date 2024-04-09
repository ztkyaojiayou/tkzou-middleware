package com.tkzou.middleware.rpc.framework;

import com.tkzou.middleware.rpc.framework.protocol.HttpClient;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvoker;
import com.tkzou.middleware.rpc.framework.protocol.ServiceInstance;

/**
 * 模拟单实例的服务提供者，主要就是封装ServiceInstance
 * 通过http发送请求
 * 以执行目标方法
 *
 * @author zoutongkun
 */
public class HttpMethodInvoker implements MethodInvoker {

    private ServiceInstance serviceInstance;

    public HttpMethodInvoker(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public String invoke(MethodInvocation methodInvocation) {
        HttpClient httpClient = new HttpClient();
        return httpClient.send(serviceInstance.getHostname(), serviceInstance.getPort(), methodInvocation);
    }
}
