package com.tkzou.middleware.rpc.framework;

import com.tkzou.middleware.rpc.framework.protocol.HttpProtocol;

/**
 * 发送远程请求的方式/协议
 * 这里先只实现http的方式，后续可加上netty的方式
 *
 * @author zoutongkun
 */
public class ProtocolFactory {
    public static Protocol getRpcProtocol(String name) {
        switch (name) {
            case "http":
                return new HttpProtocol();
            case "netty":
                return null;
            default:
                break;
        }

        return new HttpProtocol();
    }
}
