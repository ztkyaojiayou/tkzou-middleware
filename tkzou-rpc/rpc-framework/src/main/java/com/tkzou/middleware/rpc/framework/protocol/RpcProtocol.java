package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;

/**
 * rpc协议：http或dubbo
 *
 * @author zoutongkun
 */
public interface RpcProtocol {
    /**
     * 暴露服务
     * 即服务注册，由服务提供者在启动时调用
     *
     * @param serviceInstance
     */
    void export(ServiceInstance serviceInstance);

    /**
     * 引用服务
     * 即服务发现，由服务消费者在使用时调用
     * 最终生成一个调用rpc方法的RpcMethodInvoker
     *
     * @param serviceInstance
     * @return
     */
    MethodInvoker refer(ServiceInstance serviceInstance);
}
