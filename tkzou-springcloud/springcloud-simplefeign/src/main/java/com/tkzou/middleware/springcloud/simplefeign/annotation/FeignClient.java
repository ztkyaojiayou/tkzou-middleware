package com.tkzou.middleware.springcloud.simplefeign.annotation;

import java.lang.annotation.*;

/**
 * Feign客户端注解
 *
 * @author zoutongkun
 * @date 2022/4/7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {

    /**
     * 服务提供者应用名称
     *
     * @return
     */
    String value();
}
