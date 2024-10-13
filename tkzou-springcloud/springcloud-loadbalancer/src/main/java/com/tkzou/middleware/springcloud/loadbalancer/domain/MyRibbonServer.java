package com.tkzou.middleware.springcloud.loadbalancer.domain;

import com.netflix.loadbalancer.Server;

/**
 * 自定义ribbon服务实例
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyRibbonServer extends Server {
    /**
     * 构造器
     *
     * @param serviceName
     * @param host
     * @param port
     */
    public MyRibbonServer(String serviceName, String host, int port) {
        super(serviceName, host, port);
    }
}
