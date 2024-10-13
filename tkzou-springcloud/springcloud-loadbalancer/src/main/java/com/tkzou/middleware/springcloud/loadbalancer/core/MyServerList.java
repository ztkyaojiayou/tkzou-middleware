package com.tkzou.middleware.springcloud.loadbalancer.core;

import cn.hutool.core.thread.ThreadUtil;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private static final ScheduledThreadPoolExecutor scheduledExecutor =
        ThreadUtil.createScheduledExecutor(1);
    /**
     * 服务名称，也即服务id
     */
    private String serviceName;
    /**
     * 当前服务名称的所有服务实例信息
     */
    public static volatile List<MyRibbonServer> SERVICE_INFO_LIST = new CopyOnWriteArrayList<>();

    public MyServerList(MyDiscoveryProperties discoveryProperties) {
        this.discoveryProperties = discoveryProperties;
        //每10s定时刷新服务注册信息
        scheduledExecutor.scheduleWithFixedDelay(this::pullServiceInfoFromRemoteRegister, 10, 10,
            TimeUnit.SECONDS);
    }

    /**
     * 查询服务实例列表
     *
     * @return
     */
    @Override
    public List<MyRibbonServer> getInitialListOfServers() {
        return SERVICE_INFO_LIST;
    }

    /**
     * 查询服务实例列表
     *
     * @return
     */
    @Override
    public List<MyRibbonServer> getUpdatedListOfServers() {
        return SERVICE_INFO_LIST;
    }

    /**
     * 从注册中心定时刷新当前服务名称的服务实例信息
     *
     * @return
     */
    private void pullServiceInfoFromRemoteRegister() {
        Map<String, Object> param = new HashMap<>();
        param.put("serviceName", serviceName);
        //通过http发送接口请求
        String response = HttpUtil.get(discoveryProperties.getConfigServerUrl() + "/list", param);
        logger.info("query service instance, serviceId: {}, response: {}", serviceName, response);
        //解析http请求结果
        //先清理
        SERVICE_INFO_LIST.clear();
        //再添加
        SERVICE_INFO_LIST.addAll(JSON.parseArray(response).stream().map(hostInfo -> {
            String ip = ((JSONObject) hostInfo).getString("ip");
            Integer port = ((JSONObject) hostInfo).getInteger("port");
            return new MyRibbonServer(serviceName, ip, port);
        }).collect(Collectors.toList()));
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        this.serviceName = iClientConfig.getClientName();
    }
}
