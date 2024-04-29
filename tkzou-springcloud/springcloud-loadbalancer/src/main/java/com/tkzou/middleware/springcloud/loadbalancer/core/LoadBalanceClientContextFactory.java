package com.tkzou.middleware.springcloud.loadbalancer.core;

import com.tkzou.middleware.springcloud.loadbalancer.config.RibbonClientConfiguration;
import com.tkzou.middleware.springcloud.loadbalancer.config.RibbonClientSpecification;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 为每个负载均衡客户端创建一个ioc容器（ApplicationContext）
 * 实现配置的定制化！
 * 是在RibbonAutoConfiguration自动配置类中注入的
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class LoadBalanceClientContextFactory extends NamedContextFactory<RibbonClientSpecification> {

    private static final String NAMESPACE = "ribbon";

    public LoadBalanceClientContextFactory() {
        super(RibbonClientConfiguration.class, NAMESPACE, "ribbon.client.name");
    }

    @Override
    public <C> C getInstance(String name, Class<C> type) {
        return super.getInstance(name, type);
    }

    @Override
    protected AnnotationConfigApplicationContext getContext(String name) {
        return super.getContext(name);
    }
}
