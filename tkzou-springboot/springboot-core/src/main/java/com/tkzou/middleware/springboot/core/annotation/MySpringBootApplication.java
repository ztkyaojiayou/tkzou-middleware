package com.tkzou.middleware.springboot.core.annotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * springboot启动类
 *
 * @author zoutongkun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootConfiguration
@ComponentScan
@EnableAutoConfiguration
public @interface MySpringBootApplication {
}
