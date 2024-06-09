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
    public static MethodInvocation build(String interfaceName, String methodName, Object[] params, Class<?>[] paramType) {
        return new MethodInvocation(interfaceName, methodName, params, paramType);
    }
}
