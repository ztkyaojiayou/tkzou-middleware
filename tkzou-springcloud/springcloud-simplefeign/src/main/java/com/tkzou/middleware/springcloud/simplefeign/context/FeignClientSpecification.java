package com.tkzou.middleware.springcloud.simplefeign.context;

import lombok.Data;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 各服务自己的配置类封装
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
@Data
public class FeignClientSpecification implements NamedContextFactory.Specification {
    /**
     * 当前服务名
     */
    private String serviceName;
    /**
     * 当前服务名自己独有的配置类
     * 由用户在配置文件中配置！
     */
    private Class<?>[] configuration;

    public FeignClientSpecification(String serviceName, Class<?>[] configuration) {
        this.serviceName = serviceName;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return serviceName;
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }
}
