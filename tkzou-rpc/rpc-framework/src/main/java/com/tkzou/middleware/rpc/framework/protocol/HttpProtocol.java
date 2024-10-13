package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;
import com.tkzou.middleware.rpc.framework.consumer.RpcMethodInvoker;
import com.tkzou.middleware.rpc.framework.register.LocalRegister;
import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

/**
 * http方式发送请求
 *
 * @author zoutongkun
 */
public class HttpProtocol implements RpcProtocol {

    @Override
    public void export(ServiceInstance serviceInstance) {
        //1.服务注册
        //1.1本地注册
        LocalRegister.register(serviceInstance.getInterfaceName(), serviceInstance.getImplClass());
        //1.2注册中心注册
        RemoteServiceRegister.register(serviceInstance.getInterfaceName(), serviceInstance);
        //2.启动Tomcat
        HttpServer.start(serviceInstance.getHostname(), serviceInstance.getPort());
    }

    @Override
    public MethodInvoker refer(ServiceInstance serviceInstance) {
        return new RpcMethodInvoker(serviceInstance);
    }

}
