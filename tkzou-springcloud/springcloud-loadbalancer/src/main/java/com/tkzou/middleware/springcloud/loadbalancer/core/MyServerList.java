package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import com.tkzou.middleware.springcloud.loadbalancer.domain.MyRibbonServer;
import com.tkzou.middleware.springcloud.registercenter.client.config.MyDiscoveryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询服务实例列表
 * 从远程注册中心获取
 * 通过http发送请求
 * <p>
 * 查的是当前serviceName对应的所有服务实例信息
 * 这理论上是由注册中心去适配的，
 * 但这里因为没有专门的注册中心，因此这里就直接实现了，
 * 本质就是去获取所有的服务信息
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyServerList extends AbstractServerList<MyRibbonServer> {
    private static Logger logger = LoggerFactory.getLogger(MyServerList.class);

    private MyDiscoveryProperties discoveryProperties;

    private String serviceId;

    public MyServerList(MyDiscoveryProperties discoveryProperties) {
        this.discoveryProperties = discoveryProperties;
    }

    /**
     * 查询服务实例列表
     *
     * @return
     */
    @Override
    public List<MyRibbonServer> getInitialListOfServers() {
        return getServer();
    }

    /**
     * 查询服务实例列表
     *
     * @return
     */
    @Override
    public List<MyRibbonServer> getUpdatedListOfServers() {
        return getServer();
    }

    private List<MyRibbonServer> getServer() {
        Map<String, Object> param = new HashMap<>();
        param.put("serviceName", serviceId);
        //通过http发送接口请求
        String response = HttpUtil.get(discoveryProperties.getConfigServerUrl() + "/list", param);
        logger.info("query service instance, serviceId: {}, response: {}", serviceId, response);
        //解析http请求结果
        return JSON.parseArray(response).stream().map(hostInfo -> {
            String ip = ((JSONObject) hostInfo).getString("ip");
            Integer port = ((JSONObject) hostInfo).getInteger("port");
            return new MyRibbonServer(ip, port);
        }).collect(Collectors.toList());
    }

    public String getServiceId() {
        return serviceId;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        this.serviceId = iClientConfig.getClientName();
    }
}
