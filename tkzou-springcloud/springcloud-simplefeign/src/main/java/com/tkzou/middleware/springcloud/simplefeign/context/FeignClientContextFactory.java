package com.tkzou.middleware.springcloud.simplefeign.context;

import com.tkzou.middleware.springcloud.simplefeign.config.FeignClientConfiguration;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 为每个feign客户端创建一个ioc容器(ApplicationContext)，
 * 隔离每个feign客户端的配置
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
public class FeignClientContextFactory extends NamedContextFactory<FeignClientSpecification> {

    public FeignClientContextFactory() {
        super(FeignClientConfiguration.class, "feign", "feign.client.name");
    }
}
