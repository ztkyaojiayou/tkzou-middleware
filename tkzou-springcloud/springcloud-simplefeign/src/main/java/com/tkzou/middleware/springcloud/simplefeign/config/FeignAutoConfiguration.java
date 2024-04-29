package com.tkzou.middleware.springcloud.simplefeign.config;

import com.tkzou.middleware.springcloud.simplefeign.context.FeignClientContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign的自动配置类
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
@Configuration
public class FeignAutoConfiguration {

    @Bean
    public FeignClientContextFactory feignContext() {
        return new FeignClientContextFactory();
    }
}
