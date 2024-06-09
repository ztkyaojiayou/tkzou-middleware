package com.tkzou.middleware.rpc.framework.protocol;

/**
 * 发送远程请求的方式/协议
 * 这里先只实现http的方式，后续可加上netty的方式
 *
 * @author zoutongkun
 */
public class ProtocolFactory {

    public static final String NETTY = "netty";

    public static RpcProtocol getRpcProtocol(String name) {
        if (NETTY.equals(name)) {
            throw new RuntimeException("当前协议正在开发适配中，敬请期待。。。。");
        }
        return new HttpProtocol();
    }
}
