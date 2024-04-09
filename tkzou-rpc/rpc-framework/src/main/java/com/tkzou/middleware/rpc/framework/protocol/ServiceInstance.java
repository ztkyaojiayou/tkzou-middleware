package com.tkzou.middleware.rpc.framework.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务实例信息
 * 它是可以直接调用方法的！
 * 需要能序列化
 *
 * @author zoutongkun
 */
@Data
@AllArgsConstructor
public class ServiceInstance implements Serializable {
    /**
     * 协议类型
     */
    private String protocol;
    /**
     * 服务提供者的url
     */
    private String hostname;
    /**
     * 服务提供者的端口
     */
    private Integer port;
    /**
     * 接口名（这里也可以理解为服务名）
     */
    private String interfaceName;
    /**
     * 服务实现类的clazz对象
     */
    private Class implClass;

    /**
     * 构建服务实例信息
     *
     * @param protocol
     * @param hostname
     * @param port
     * @param interfaceName
     * @param implClass
     * @return
     */
    public static ServiceInstance build(String protocol, String hostname, Integer port, String interfaceName,
                                        Class implClass) {
        return new ServiceInstance(protocol, hostname, port, interfaceName, implClass);
    }
}
