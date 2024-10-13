package com.tkzou.middleware.rpc.framework.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务提供者的接口的方法元信息（相当于方法签名信息），用于反射调用！
 * 即通过如下字段确定一个接口的某个方法
 * 易知，对于rpc调用就只差一个服务实例的url和端口啦！！！
 * 因为是需要通过网络传递，因此需要能序列化和反序列化
 * 我们这里就使用简单的jdk序列化机制实现，在实际的rpc框架中，可以定制高性能的序列化机制！
 * 注意：而其实在真正的rpc框架如feign中，要访问的rpc目标方法都是通过在一个注解上直接声明的，
 * 即声明访问目标服务的哪一个接口，而无需具体指定对应的方法名和参数，
 * 只是在调用前，需要先从声明的rpc接口上将入参赋值给目标接口，待返回时再把结果组装成rpc接口中指定的返回值，在feign中，它是在spring启动期间先把所有声明的rpc
 * 接口的这些信息先解析和保存好（本质就是个map），同时为rpc接口都生成一个代理对象，在增强逻辑中完成rpc调用和返回值组装，相关元信息就直接在之前解析和保存好的map中取用即可，就这么简单！
 *
 * @author zoutongkun
 */
@Data
@AllArgsConstructor
public class MethodInvocation implements Serializable {
    /**
     * 接口名--即要调用哪个接口
     */
    private String interfaceName;
    /**
     * 方法名--即要调用当前接口的哪个方法
     */
    private String methodName;
    /**
     * 该方法的参数--即该方法的入参信息
     */
    private Object[] params;
    /**
     * 该方法的参数类型--即该方法的入参类型
     */
    private Class<?>[] paramType;

    /**
     * 构建Invocation
     *
     * @param interfaceName
     * @param methodName
     * @param params
     * @param paramType
     * @return
     */
    public static MethodInvocation build(String interfaceName, String methodName, Object[] params
        , Class<?>[] paramType) {
        return new MethodInvocation(interfaceName, methodName, params, paramType);
    }
}
