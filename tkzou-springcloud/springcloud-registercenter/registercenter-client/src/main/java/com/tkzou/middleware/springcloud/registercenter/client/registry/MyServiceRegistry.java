package com.tkzou.middleware.springcloud.registercenter.client.registry;

import cn.hutool.http.HttpUtil;
import com.tkzou.middleware.springcloud.registercenter.client.config.MyDiscoveryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 具体的服务注册实现类
 * 会统一在ServiceRegisterCoreBeanAutoConfiguration类中加载为bean！！！
 * 只有该类有实例对象/bean时才可能调用它的方法呀！！！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyServiceRegistry implements ServiceRegistry<Registration> {
    private static final Logger logger = LoggerFactory.getLogger(MyServiceRegistry.class);

    private MyDiscoveryProperties myDiscoveryProperties;

    /**
     * 构造器
     *
     * @param myDiscoveryProperties
     */
    public MyServiceRegistry(MyDiscoveryProperties myDiscoveryProperties) {
        this.myDiscoveryProperties = myDiscoveryProperties;
    }

    /**
     * 注册服务实例
     * 注册到注册中心，注册中心这里没有使用自己的，
     * 只是通过一个url示例了一下，将这个url理解为nacos即可！
     * 何时调用？在springboot发布WebServerInitializedEvent事件时调用！！！
     * 具体是由AbstractAutoServiceRegistration监听并调用！
     *
     * @param registration 也即TutuRegistration，需要先注册为bean
     */
    @Override
    public void register(Registration registration) {
        Map<String, Object> param = new HashMap<>();
        //获取当前服务的服务信息，它属于各个服务的配置项！
        param.put("serviceName", registration.getServiceId());
        param.put("ip", registration.getHost());
        param.put("port", registration.getPort());
        //通过http的方式注册到远程的注册中心如nacos
        String result = HttpUtil.post(myDiscoveryProperties.getConfigServerUrl() + "/register",
            param);
        if (Boolean.parseBoolean(result)) {
            logger.info("register service successfully, serviceName: {}, ip: {}, port: {}",
                registration.getServiceId(), registration.getHost(), registration.getPort());
        } else {
            logger.error("register service failed, serviceName: {}, ip: {}, port: {}",
                registration.getServiceId(), registration.getHost(), registration.getPort());
            throw new RuntimeException("register service failed, serviceName");
        }
    }

    /**
     * 注销服务实例
     *
     * @param registration
     */
    @Override
    public void deregister(Registration registration) {
        Map<String, Object> param = new HashMap<>();
        param.put("serviceName", myDiscoveryProperties.getServiceName());
        param.put("ip", myDiscoveryProperties.getIp());
        param.put("port", myDiscoveryProperties.getPort());

        String result = HttpUtil.post(myDiscoveryProperties.getConfigServerUrl() + "/deregister",
            param);
        if (Boolean.parseBoolean(result)) {
            logger.info("de-register service successfully, serviceName: {}, ip: {}, port: {}",
                myDiscoveryProperties.getServiceName(), myDiscoveryProperties.getIp(),
                myDiscoveryProperties.getPort());
        } else {
            logger.warn("de-register service failed, serviceName: {}, ip: {}, port: {}",
                myDiscoveryProperties.getServiceName(), myDiscoveryProperties.getIp(),
                myDiscoveryProperties.getPort());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void setStatus(Registration registration, String status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getStatus(Registration registration) {
        return null;
    }
}
