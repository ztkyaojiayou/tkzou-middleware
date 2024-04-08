package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.MethodInvoker;
import com.tkzou.middleware.rpc.framework.Protocol;
import com.tkzou.middleware.rpc.framework.ServiceInstance;
import com.tkzou.middleware.rpc.framework.register.LocalRegister;
import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

/**
 * http方式发送请求
 *
 * @author zoutongkun
 */
public class HttpProtocol implements Protocol {

    @Override
    public void export(ServiceInstance serviceInstance) {
        // 本地注册
        LocalRegister.register(serviceInstance.getInterfaceName(), serviceInstance.getImplClass());
        // 注册中心注册
        RemoteServiceRegister.register(serviceInstance.getInterfaceName(), serviceInstance);
        // 启动Tomcat
        new HttpServer().start(serviceInstance.getHostname(), serviceInstance.getPort());
    }

    @Override
    public MethodInvoker refer(ServiceInstance serviceInstance) {
        return new HttpMethodInvoker(serviceInstance);
    }

}
