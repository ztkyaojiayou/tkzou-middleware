package com.tkzou.middleware.springcloud.registercenter.client.config;

import com.tkzou.middleware.springcloud.registercenter.client.discovery.MyDiscoveryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置服务发现相关类
 * 将相关的bean注册到ioc容器！！！
 * 后面再由spi统一导出到使用该依赖的项目！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@Configuration
public class MyDiscoveryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyDiscoveryProperties myDiscoveryProperties() {
        return new MyDiscoveryProperties();
    }

    @Bean
    public MyDiscoveryClient myDiscoveryClient(MyDiscoveryProperties myDiscoveryProperties) {
        return new MyDiscoveryClient(myDiscoveryProperties);
    }
}
