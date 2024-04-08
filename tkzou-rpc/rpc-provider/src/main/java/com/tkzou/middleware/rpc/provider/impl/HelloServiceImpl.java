package com.tkzou.middleware.rpc.provider.impl;

import com.tkzou.middleware.rpc.provider.api.HelloService;

/**
 * @author zoutongkun
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String userName) {
        return "Hello: " + userName;
    }
}
