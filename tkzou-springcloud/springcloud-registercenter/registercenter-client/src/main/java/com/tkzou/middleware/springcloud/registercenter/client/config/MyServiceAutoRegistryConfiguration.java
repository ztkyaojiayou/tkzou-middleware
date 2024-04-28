package com.tkzou.middleware.springcloud.registercenter.client.config;

import com.tkzou.middleware.springcloud.registercenter.client.registry.MyRegistration;
import com.tkzou.middleware.springcloud.registercenter.client.registry.MyServiceAutoRegistryProcessor;
import com.tkzou.middleware.springcloud.registercenter.client.registry.MyServiceRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置服务注册相关类
 * 将相关的bean注册到ioc容器！！！
 * 后面再由spi统一导出到使用该依赖的项目！
 * 这已经是基操了！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
@Configuration
//适配springcloud的enable功能
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
public class MyServiceAutoRegistryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyDiscoveryProperties myProperties() {
        return new MyDiscoveryProperties();
    }

    @Bean
    public MyRegistration myRegistration(MyDiscoveryProperties myDiscoveryProperties) {
        return new MyRegistration(myDiscoveryProperties);
    }

    @Bean
    public MyServiceRegistry myServiceRegistry(MyDiscoveryProperties myDiscoveryProperties) {
        return new MyServiceRegistry(myDiscoveryProperties);
    }

    @Bean
    public MyServiceAutoRegistryProcessor myAutoServiceRegistration(ServiceRegistry<Registration> serviceRegistry, MyRegistration myRegistration) {
        return new MyServiceAutoRegistryProcessor(serviceRegistry, myRegistration);
    }
}
