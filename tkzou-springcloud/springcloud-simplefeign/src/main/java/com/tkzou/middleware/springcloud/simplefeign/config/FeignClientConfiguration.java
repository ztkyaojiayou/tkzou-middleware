package com.tkzou.middleware.springcloud.simplefeign.config;

import com.tkzou.middleware.springcloud.simplefeign.core.SpringMvcContract;
import com.tkzou.middleware.springcloud.simplefeign.ribbon.LoadBalancerFeignClient;
import feign.Client;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置feign的核心API
 * 这些bean是feign在为带有@FeignClient注解的接口生成代理对象时所必须的！
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
@Configuration
public class FeignClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Encoder encoder() {
        return new Encoder.Default();
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder decoder() {
        return new Decoder.Default();
    }

    @Bean
    @ConditionalOnMissingBean
    public Contract contract() {
        return new SpringMvcContract();
    }

    /**
     * 注入一个发送http请求的具体实现，使用的就是open-feign的默认实现：Default
     * LoadBalancerClient这个bean是在负载均衡模块初始化的！
     * 这里直接注入使用即可，因为当前模块是依赖了负载均衡模块的！
     *
     * @param loadBalancerClient
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Client client(LoadBalancerClient loadBalancerClient) {
        return new LoadBalancerFeignClient(loadBalancerClient, new Client.Default(null, null));
    }
}
