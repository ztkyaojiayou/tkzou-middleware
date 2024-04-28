package com.tkzou.middleware.springcloud.registercenter.server.controller;

import com.alibaba.fastjson.JSON;
import com.tkzou.middleware.springcloud.registercenter.server.domain.ServerInfo;
import com.tkzou.middleware.springcloud.registercenter.server.registrycenter.LocalRegistryCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册中心
 *
 * @author zoutongkun
 */
@RestController
public class RegistryServerController {
    private static Logger logger = LoggerFactory.getLogger(RegistryServerController.class);
    /**
     * 用于保存所有的服务信息
     * key:服务名称
     * value：当前服务下的所有服务实例
     */
    private ConcurrentHashMap<String, Set<ServerInfo>> serverMap = new ConcurrentHashMap<>();

    /**
     * 服务注册
     *
     * @param serviceName
     * @param ip
     * @param port
     * @return
     */
    @PostMapping("register")
    public boolean register(@RequestParam("serviceName") String serviceName, @RequestParam("ip") String ip, @RequestParam("port") Integer port) {
        logger.info("register service, serviceName: {}, ip: {}, port: {}", serviceName, ip, port);
        LocalRegistryCenter.register(serviceName, ip, port);
        return true;
    }

    /**
     * 服务注销
     *
     * @param serviceName
     * @param ip
     * @param port
     * @return
     */
    @PostMapping("deregister")
    public boolean deregister(@RequestParam("serviceName") String serviceName, @RequestParam("ip") String ip, @RequestParam("port") Integer port) {
        logger.info("deregister service, serviceName: {}, ip: {}, port: {}", serviceName, ip, port);
        LocalRegistryCenter.deregister(serviceName, ip, port);
        return true;
    }

    /**
     * 根据服务名称查询服务列表
     *
     * @param serviceName
     * @return
     */
    @GetMapping("list")
    public Set<ServerInfo> list(@RequestParam("serviceName") String serviceName) {
        logger.info("list service, serviceName: {}, serverSet: {}", serviceName, JSON.toJSONString(serviceName));
        return LocalRegistryCenter.list(serviceName);
    }

    /**
     * 查询所有服务名称列表
     *
     * @return
     */
    @GetMapping("listServiceNames")
    public Enumeration<String> listServiceNames() {
        return LocalRegistryCenter.listAllServiceNames();
    }

}
