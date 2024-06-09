package com.tkzou.middleware.rpc.framework.loadbalance;

import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;

import java.util.List;

/**
 * 负载均衡策略
 *
 * @author zoutongkun
 */
public interface IRule {
    /**
     * 选取一个实例
     *
     * @param methodInvokers 所有实例
     * @return
     */
    MethodInvoker choose(List<MethodInvoker> methodInvokers);
}
