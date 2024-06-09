package com.tkzou.middleware.rpc.framework.proxy;

import com.tkzou.middleware.rpc.framework.loadbalance.LoadBalanceStrategy;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;
import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * rpc代理工厂，用于生成rpc接口的代理对象
 * 这里没有统一将服务提供者的所有接口统一代理并保存，
 * 但在真正的rpc框架中一般都是在服务启动时通过在ioc容器初始化时提供的扩展点来
 * 统一扫描目标接口以生成代理对象并保存备用的（即代理对象的初始化！！！），而不是临时生成！！！
 * mybatis如此，feign也如此！
 *
 * @author zoutongkun
 */
public class RpcProxyFactory<T> {
    /**
     * 获取rpc接口的代理对象
     * 同时定义代理逻辑，这里很明显就是发送http请求到服务提供者处理并返回结果！！！
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(final Class interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new RpcProxy(interfaceClass));
    }

}
