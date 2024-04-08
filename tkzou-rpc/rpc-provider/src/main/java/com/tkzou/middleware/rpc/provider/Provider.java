package com.tkzou.middleware.rpc.provider;

import com.tkzou.middleware.rpc.framework.Protocol;
import com.tkzou.middleware.rpc.framework.ProtocolFactory;
import com.tkzou.middleware.rpc.framework.ServiceInstance;
import com.tkzou.middleware.rpc.provider.api.HelloService;
import com.tkzou.middleware.rpc.provider.impl.HelloServiceImpl;

/**
 * 启动服务提供者
 * 需要先启动
 * @author zoutongkun
 */
public class Provider {

    private static boolean isRun = true;

    public static void main(String[] args) {

        String protocolName = System.getProperty("protocol");

        ServiceInstance serviceInstance = ServiceInstance.build(protocolName, "localhost", 8080, HelloService.class.getName(), HelloServiceImpl.class);

        Protocol protocol = ProtocolFactory.getRpcProtocol(protocolName);
        protocol.export(serviceInstance);

    }
}
