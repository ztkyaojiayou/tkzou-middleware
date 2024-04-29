package com.tkzou.middleware.springcloud.simplefeign;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置bean统一导出
 *
 * @author zoutongkun
 */
@Configuration
@ComponentScan("com.tkzou.middleware.springcloud.simplefeign")
public class SimpleFeignBeanAutoConfiguration {
}
