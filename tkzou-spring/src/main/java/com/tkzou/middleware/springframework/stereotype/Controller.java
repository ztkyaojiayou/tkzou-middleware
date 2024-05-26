package com.tkzou.middleware.springframework.stereotype;

import java.lang.annotation.*;

/**
 * 衍生的@Component注解
 * @author zoutongkun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    String value() default "";
}