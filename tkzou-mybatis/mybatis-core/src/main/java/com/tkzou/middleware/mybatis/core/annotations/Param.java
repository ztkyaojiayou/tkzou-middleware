package com.tkzou.middleware.mybatis.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> mapper方法参数映射 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value();

}
