package com.tkzou.middleware.rpc.provider.api;

/**
 * 服务提供者的接口
 * 消费者最终也是调用该接口的具体实现类
 *
 * @author zoutongkun
 */
public interface TestRpcService {
    /**
     * 业务方法
     *
     * @param userName
     * @return
     */
    String test(String userName);
}
