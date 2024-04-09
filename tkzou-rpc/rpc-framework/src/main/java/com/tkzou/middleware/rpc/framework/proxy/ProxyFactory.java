package com.tkzou.middleware.rpc.framework.proxy;

import com.tkzou.middleware.rpc.framework.loadbalance.LoadBalanceStrategy;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvoker;
import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 代理工厂，用于生成接口的代理对象
 * 这里没有统一将服务提供者的所有接口统一代理并保存，
 * 但在真正的rpc框架中一般都是在服务启动时通过在ioc容器初始化时提供的扩展点来
 * 统一扫描目标接口以生成代理对象并保存备用的（即代理对象的初始化！！！），而不是临时生成！！！
 * mybatis如此，feign也如此！
 *
 * @author zoutongkun
 */
public class ProxyFactory<T> {
    /**
     * 获取接口的代理对象
     * 同时定义代理逻辑，这里很明显就是发送http请求到服务提供者处理并返回结果！！！
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(final Class interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy,
                                                                                                         method, args) -> {
            //代理逻辑：就是发送http请求到服务提供者并获取返回结果！
            //1.模拟mock，可以从配置文件中读取
            String mock = System.getProperty("mock");
            if (mock != null && mock.startsWith("return:")) {
                return mock.replace("return:", "");
            }
            //2.构造invocation对象，发送http请求到服务提供者
            //这是我们的框架约定，即在框架层会解析该对象并对服务提供者发起rpc调用（本质就是个http请求！！！）
            MethodInvocation methodInvocation = MethodInvocation.build(interfaceClass.getName(), method.getName(), args,
                    method.getParameterTypes());
            //3.从注册中心获取所有的服务实例
            List<MethodInvoker> invokerList = RemoteServiceRegister.getCandidateMethodInvokers(interfaceClass);
            //4.再从服务实例列表中选一台调用，易知这就涉及到服务的负载均衡啦！
            MethodInvoker methodInvoker = LoadBalanceStrategy.getByDefaultStrategy(invokerList);
            return methodInvoker.invoke(methodInvocation);
        });
    }

}
