package com.tkzou.middleware.mybatis.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 用于配置多个Signature注解 </p>
 * 可以配置多个Signature，每个Signature就代表一个方法，因此该注解上可以配置多个方法！
 * 在解析该注解时，需要把这些方法都保存起来以便判断目标方法是否在这个集合中，
 * 若在，则走拦截器，否则放行！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {

    Signature[] value();

}
