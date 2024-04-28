package com.tkzou.middleware.springcloud.registercenter.client.discovery;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * 服务实例信息的具体实现
 * 主要用于封装服务发现时或负载均衡器发现时获取到的服务信息
 * 而非服务注册时使用，服务注册时使用另一个实现类来封装，也即MyRegistration
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyServiceInstance implements ServiceInstance {

    private String serviceId;

    private String host;

    private int port;

    private boolean secure = false;

    /**
     * 额外元信息
     */
    private Map<String, String> metadata;

    public MyServiceInstance() {
    }

    public MyServiceInstance(String serviceId, String host, int port) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
