package com.tkzou.middleware.rpc.framework.protocol;

/**
 * 方法执行器
 * 实现类中已包含服务提供者的url和端口
 *
 * @author zoutongkun
 */
public interface MethodInvoker {
    /**
     * 执行方法，最终会发起rpc调用
     * 本质就是一次http请求
     *
     * @param methodInvocation
     * @return
     */
    String invoke(MethodInvocation methodInvocation);
}
