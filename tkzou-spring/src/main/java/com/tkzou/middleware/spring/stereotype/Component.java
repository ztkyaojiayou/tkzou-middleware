package com.tkzou.middleware.spring.stereotype;

import java.lang.annotation.*;

/**
 * 用于标识一个bean的注解
 *
 * @author :zoutongkun
 * @date :2024/5/25 4:33 下午
 * @description :
 * @modyified By:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String value() default "";
}