package com.tkzou.middleware.springcloud.registercenter.client.registry;

import com.tkzou.middleware.springcloud.registercenter.client.config.MyDiscoveryProperties;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.Map;

/**
 * 注册时的服务实例封装
 * 会统一在ServiceRegisterCoreBeanAutoConfiguration类中加载为bean
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyRegistration implements Registration {

    private MyDiscoveryProperties myDiscoveryProperties;

    /**
     * 构造器
     *
     * @param myDiscoveryProperties
     */
    public MyRegistration(MyDiscoveryProperties myDiscoveryProperties) {
        this.myDiscoveryProperties = myDiscoveryProperties;
    }

    @Override
    public String getServiceId() {
        return myDiscoveryProperties.getServiceName();
    }

    @Override
    public String getHost() {
        return myDiscoveryProperties.getIp();
    }

    @Override
    public int getPort() {
        return myDiscoveryProperties.getPort();
    }

    public void setPort(int port) {
        this.myDiscoveryProperties.setPort(port);
    }

    @Override
    public boolean isSecure() {
        return myDiscoveryProperties.isSecure();
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    @Override
    public Map<String, String> getMetadata() {
        return null;
    }

    public MyDiscoveryProperties getMyDiscoveryProperties() {
        return myDiscoveryProperties;
    }
}
