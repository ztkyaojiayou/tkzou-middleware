package com.tkzou.middleware.springcloud.simplefeign.annotation;

import com.tkzou.middleware.springcloud.simplefeign.core.FeignClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Feign注解
 *
 * @author zoutongkun
 * @date 2022/4/7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignClientsRegistrar.class)
public @interface EnableFeignClients {
}
