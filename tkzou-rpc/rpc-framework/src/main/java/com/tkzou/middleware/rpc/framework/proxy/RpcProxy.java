package com.tkzou.middleware.rpc.framework.proxy;

import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;
import com.tkzou.middleware.rpc.framework.loadbalance.LoadBalanceStrategy;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
import com.tkzou.middleware.rpc.framework.register.RemoteServiceRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * rpc接口的代理类的增强逻辑
 *
 * @author :zoutongkun
 * @date :2024/6/9 3:50 下午
 * @description :
 * @modyified By:
 */
public class RpcProxy implements InvocationHandler {
    /**
     * 要代理的rpc接口的class
     */
    private Class<?> interfaceClass;

    public RpcProxy(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //代理逻辑：就是发送http请求到服务提供者并获取返回结果！
        //1.模拟mock，可以从配置文件中读取
        String mock = System.getProperty("mock");
        if (mock != null && mock.startsWith("return:")) {
            return mock.replace("return:", "");
        }
        //2.构造invocation对象，发送http请求到服务提供者
        //这是我们的框架约定，即在框架层会解析该对象并对服务提供者发起rpc调用（本质就是个http请求！！！）
        MethodInvocation methodInvocation = MethodInvocation.build(interfaceClass.getName(),
            method.getName(), args,
            method.getParameterTypes());
        //3.从注册中心获取所有的服务实例
        //这里的关键就是：我们把接口名称就直接当做是一个feign中的一个rpc服务名了！！！
        //而在feign中则是专门通过一个注解中的一个字段来指定，本质相同！！！
        List<MethodInvoker> invokerList =
            RemoteServiceRegister.getCandidateMethodInvokers(interfaceClass);
        //4.再从服务实例列表中选一台调用，易知这就涉及到服务的负载均衡啦！
        MethodInvoker methodInvoker = LoadBalanceStrategy.getDefault(invokerList);
        //5.调用服务提供者，并返回结果
        //即执行rpc调用
        return methodInvoker.invoke(methodInvocation);
    }
}
