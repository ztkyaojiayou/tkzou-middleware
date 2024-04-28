package com.tkzou.middleware.springcloud.registercenter.server.registrycenter;

import com.tkzou.middleware.springcloud.registercenter.server.domain.ServerInfo;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册中心
 * 这里就只是本地注册一下
 *
 * @author zoutongkun
 */
public class LocalRegistryCenter {
    /**
     * 用于保存所有的服务信息
     * key:服务名称
     * value：当前服务下的所有服务实例
     */
    private static ConcurrentHashMap<String, Set<ServerInfo>> serverMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public static void register(String serviceName, String ip, Integer port) {
        serverMap.putIfAbsent(serviceName.toLowerCase(), Collections.synchronizedSet(new HashSet<>()));
        ServerInfo serverInfo = new ServerInfo(ip, port);
        serverMap.get(serviceName).add(serverInfo);
    }

    /**
     * 注销服务
     */
    public static void deregister(String serviceName, String ip, Integer port) {
        Set<ServerInfo> serverInfoSet = serverMap.get(serviceName.toLowerCase());
        if (serverInfoSet != null) {
            ServerInfo serverInfo = new ServerInfo(ip, port);
            serverInfoSet.remove(serverInfo);
        }
    }

    /**
     * 获取/发现服务
     *
     * @return
     */
    public static Set<ServerInfo> list(String serviceName) {
        Set<ServerInfo> serverInfoSet = serverMap.get(serviceName.toLowerCase());
        return serverInfoSet != null ? serverInfoSet : Collections.emptySet();
    }

    /**
     * 查询所有服务名称列表
     *
     * @return
     */
    public static Enumeration<String> listAllServiceNames() {
        return serverMap.keys();
    }
}
