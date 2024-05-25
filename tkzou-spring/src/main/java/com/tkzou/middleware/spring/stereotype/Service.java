package com.tkzou.middleware.spring.stereotype;

import java.lang.annotation.*;

/**
 * 衍生的@Component注解
 *
 * @author zoutongkun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}