package com.tkzou.middleware.springcloud.loadbalancer;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置bean统一导出
 *
 * @author zoutongkun
 */
@Configuration
@ComponentScan("com.tkzou.middleware.springcloud.loadbalancer")
public class LoadbalancerBeanAutoConfiguration {
}
