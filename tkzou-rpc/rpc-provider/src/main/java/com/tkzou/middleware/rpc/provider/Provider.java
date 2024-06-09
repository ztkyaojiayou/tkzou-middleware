package com.tkzou.middleware.rpc.provider;

import com.tkzou.middleware.rpc.framework.protocol.ProtocolFactory;
import com.tkzou.middleware.rpc.framework.protocol.RpcProtocol;
import com.tkzou.middleware.rpc.framework.protocol.ServiceInstance;
import com.tkzou.middleware.rpc.provider.api.TestRpcService;
import com.tkzou.middleware.rpc.provider.impl.TestRpcServiceImpl;

/**
 * 启动服务提供者，这也是一个单独的线程
 * 需要先启动
 *
 * @author zoutongkun
 */
public class Provider {

    private static boolean isRun = true;

    public static void main(String[] args) {
        String protocolName = System.getProperty("protocol");
        //1.封装当前要注册的服务为ServiceInstance
        //这里就是注册当前服务的TestRpcService接口，重点是注册了实现类！
        ServiceInstance serviceInstance = ServiceInstance.build(protocolName, "localhost", 8080,
                TestRpcService.class.getName(), TestRpcServiceImpl.class);
        RpcProtocol rpcProtocol = ProtocolFactory.getRpcProtocol(protocolName);
        //2.服务注册，同时启动tomcat
        rpcProtocol.export(serviceInstance);
    }
}
