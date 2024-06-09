package com.tkzou.middleware.rpc.framework.register;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务本地注册
 *
 * @author zoutongkun
 */
public class LocalRegister {
    /**
     * 服务信息
     * 这里就只是保存一下具体的实现类！
     */
    private static Map<String, Class<?>> SERVICE_REGISTER = new HashMap<>();

    /**
     * 注册服务
     *
     * @param interfaceName
     * @param implClass
     */
    public static void register(String interfaceName, Class implClass) {
        SERVICE_REGISTER.put(interfaceName, implClass);
    }

    /**
     * 获取/发现服务
     *
     * @param interfaceName
     * @return
     */
    public static Class get(String interfaceName) {
        return SERVICE_REGISTER.get(interfaceName);
    }
}
