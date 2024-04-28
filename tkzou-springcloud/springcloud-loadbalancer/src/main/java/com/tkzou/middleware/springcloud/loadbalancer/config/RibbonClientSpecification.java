package com.tkzou.middleware.springcloud.loadbalancer.config;

import lombok.Data;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * ribbon客户端配置
 * 每个服务都可以有自己的配置！
 * 相当于是配置定制化
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@Data
public class RibbonClientSpecification implements NamedContextFactory.Specification {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 对应的配置类，可以有多个，
     * 实际效果就是：在配置文件中可以定义多个配置类的class路径
     * 此时ribbon就会去解析这些配置类到对应服务名自己的ioc容器中！！！
     */
    private Class<?>[] configuration;

    public RibbonClientSpecification() {
    }

    public RibbonClientSpecification(String name, Class<?>[] configuration) {
        this.serviceName = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return serviceName;
    }

    public void setName(String name) {
        this.serviceName = name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

}
