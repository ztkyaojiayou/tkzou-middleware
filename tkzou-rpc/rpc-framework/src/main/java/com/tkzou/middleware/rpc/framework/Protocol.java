package com.tkzou.middleware.rpc.framework;

/**
 * rpc协议：http或dubbo
 *
 * @author zoutongkun
 */
public interface Protocol {
    /**
     * 暴露服务
     * 即服务注册
     *
     * @param serviceInstance
     */
    void export(ServiceInstance serviceInstance);

    /**
     * 引用服务
     * 即服务发现
     *
     * @param serviceInstance
     * @return
     */
    MethodInvoker refer(ServiceInstance serviceInstance);
}
