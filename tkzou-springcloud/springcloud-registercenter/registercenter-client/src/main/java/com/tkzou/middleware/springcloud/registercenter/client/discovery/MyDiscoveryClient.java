package com.tkzou.middleware.springcloud.registercenter.client.discovery;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tkzou.middleware.springcloud.registercenter.client.config.MyDiscoveryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务发现实现类
 * 即从注册中心找到当前服务名称的所有实例
 * 本质就是发送一个http请求去调接口！！！
 * 该bean在MyDiscoveryAutoConfiguration类中集中加载到ioc容器！
 * 这其实也是推荐的方式，即尽量不要通过类似@component或@configuration等注解分散到各个角落，不易维护！！！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyDiscoveryClient implements DiscoveryClient {
    private static final Logger logger = LoggerFactory.getLogger(MyDiscoveryClient.class);

    private MyDiscoveryProperties myDiscoveryProperties;

    public MyDiscoveryClient(MyDiscoveryProperties myDiscoveryProperties) {
        this.myDiscoveryProperties = myDiscoveryProperties;
    }

    /**
     * 获取当前服务的所有实例
     * 一个服务名对应多个具体的服务实例，后续再通过负载均衡先出一台具体的服务实例
     *
     * @param serviceId 指定的服务名称
     * @return
     */
    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        Map<String, Object> param = new HashMap<>();
        param.put("serviceName", serviceId);
        //从注册中心获取，使用http请求即可！
        String response = HttpUtil.get(myDiscoveryProperties.getConfigServerUrl() + "/list", param);
        logger.info("query service instance, serviceId: {}, response: {}", serviceId, response);
        //使用fastJson解析
        return JSON.parseArray(response).stream().map(hostInfo -> {
            //封装为MyServiceInstance对象
            MyServiceInstance serviceInstance = new MyServiceInstance();
            serviceInstance.setServiceId(serviceId);
            String ip = ((JSONObject) hostInfo).getString("ip");
            Integer port = ((JSONObject) hostInfo).getInteger("port");
            serviceInstance.setHost(ip);
            serviceInstance.setPort(port);
            return serviceInstance;
        }).collect(Collectors.toList());
    }

    /**
     * 获取所有的服务名信息
     * 从远程注册中心获取
     * 通过http获取
     *
     * @return
     */
    @Override
    public List<String> getServices() {
        String response = HttpUtil.post(myDiscoveryProperties.getConfigServerUrl() + "/listServiceNames", new HashMap<>());
        logger.info("query service instance list, response: {}", response);
        return JSON.parseArray(response, String.class);
    }

    @Override
    public String description() {
        return "My Spring Cloud Discovery Client";
    }
}
