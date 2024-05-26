package com.tkzou.middleware.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * bean的作用域
 * 如单例，原型等
 *
 * @author :zoutongkun
 * @date :2024/5/25 4:33 下午
 * @description :
 * @modyified By:
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    String value() default "singleton";
}
