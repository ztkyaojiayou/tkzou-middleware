package com.tkzou.middleware.rpc.framework.loadbalance;

import com.tkzou.middleware.rpc.framework.protocol.MethodInvoker;

import java.util.List;

/**
 * 负载均衡策略工厂
 *
 * @author zoutongkun
 */
public class LoadBalanceStrategy {
    /**
     * 默认策略：随机选取
     *
     * @return
     */
    public static IRule defaultStrategy() {
        return new RandomRule();
    }

    /**
     * 通过默认策略获取一台实例
     *
     * @param invokerList
     * @return
     */
    public static MethodInvoker getByDefaultStrategy(List<MethodInvoker> invokerList) {
        IRule defaultStrategy = LoadBalanceStrategy.defaultStrategy();
        return defaultStrategy.choose(invokerList);
    }

}
