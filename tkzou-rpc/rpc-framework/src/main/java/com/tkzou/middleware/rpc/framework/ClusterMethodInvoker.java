package com.tkzou.middleware.rpc.framework;

import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟多实例的服务提供者
 *
 * @author zoutongkun
 */
public class ClusterMethodInvoker implements MethodInvoker {

    private List<MethodInvoker> methodInvokers = new ArrayList<>();

    public List<MethodInvoker> getInvokers() {
        return methodInvokers;
    }

    public void addInvokers(MethodInvoker methodInvoker) {
        this.methodInvokers.add(methodInvoker);
    }

    public static MethodInvoker join(Class interfaceClass) {
        ClusterMethodInvoker clusterInvoker = new ClusterMethodInvoker();

        // 从注册中心获取所有服务实例
        List<ServiceInstance> serviceInstanceList = RemoteServiceRegister.get(interfaceClass.getName());
//解析为
        serviceInstanceList.forEach(serviceInstance -> {
            Protocol protocol = ProtocolFactory.getRpcProtocol(serviceInstance.getProtocol());
            MethodInvoker methodInvoker = protocol.refer(serviceInstance);
            clusterInvoker.addInvokers(methodInvoker);
        });

        return clusterInvoker;
    }

    @Override
    public String invoke(MethodInvocation methodInvocation) {
        MethodInvoker methodInvoker = LoadBalance.random(methodInvokers);
        return methodInvoker.invoke(methodInvocation);
    }
}
