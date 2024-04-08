package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.MethodInvocation;
import com.tkzou.middleware.rpc.framework.MethodInvoker;
import com.tkzou.middleware.rpc.framework.ServiceInstance;

/**
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
