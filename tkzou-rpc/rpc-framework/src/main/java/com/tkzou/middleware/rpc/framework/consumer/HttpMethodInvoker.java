package com.tkzou.middleware.rpc.framework.consumer;

import com.tkzou.middleware.rpc.framework.http.HttpClient;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
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

    /**
     * 执行rpc接口中的方法
     *
     * @param methodInvocation
     * @return
     */
    @Override
    public String invoke(MethodInvocation methodInvocation) {
        //方式http请求，返回结果
        HttpClient httpClient = new HttpClient();
        return httpClient.send(serviceInstance.getHostname(), serviceInstance.getPort(), methodInvocation);
    }
}
