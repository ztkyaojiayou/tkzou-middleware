package com.tkzou.middleware.rpc.provider.impl;

import com.tkzou.middleware.rpc.provider.api.TestRpcService;

/**
 * 服务提供者接口的具体实现
 *
 * @author zoutongkun
 */
public class TestRpcServiceImpl implements TestRpcService {

    @Override
    public String test(String userName) {
        return "Hello: " + userName;
    }
}
