package com.tkzou.middleware.mybatis.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 二级缓存注解 </p>
 * 跨session的缓存
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/28 05:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheNamespace {

}
