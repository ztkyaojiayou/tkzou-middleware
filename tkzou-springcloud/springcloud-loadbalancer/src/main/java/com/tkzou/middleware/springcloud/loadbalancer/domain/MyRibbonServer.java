package com.tkzou.middleware.springcloud.loadbalancer.domain;

import com.netflix.loadbalancer.Server;

/**
 * 图图服务实例
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyRibbonServer extends Server {

    public MyRibbonServer(String host, int port) {
        super(host, port);
    }
}
