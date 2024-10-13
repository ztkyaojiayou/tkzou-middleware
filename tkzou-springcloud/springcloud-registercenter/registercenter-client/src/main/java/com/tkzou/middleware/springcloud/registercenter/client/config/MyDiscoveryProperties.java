package com.tkzou.middleware.springcloud.registercenter.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * 服务注册配置类，即当前服务的信息，用于注册！
 * 会统一在ServiceRegisterCoreBeanAutoConfiguration类中加载为bean！！！
 * 这里就不加@configuration或@component注解了
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@ConfigurationProperties("spring.cloud.tkzou.discovery")
public class MyDiscoveryProperties {

    @Autowired
    private InetUtils inetUtils;
    /**
     * 远程注册中心的url
     * ip+port
     */
    private String configServerUrl;
    /**
     * 当前服务的服务名称
     */
    private String serviceName;
    /**
     * 当前服务的ip地址
     */
    private String ip;
    /**
     * 当前服务的端口号
     */
    private int port = -1;

    private boolean secure = false;

    /**
     * 初始化
     * 拿到当前服务的ip
     */
    @PostConstruct
    public void init() {
        if (!StringUtils.hasLength(ip)) {
            //获取当前服务的IP地址
            ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        }
    }

    public String getConfigServerUrl() {
        return configServerUrl;
    }

    public void setConfigServerUrl(String configServerUrl) {
        this.configServerUrl = configServerUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
