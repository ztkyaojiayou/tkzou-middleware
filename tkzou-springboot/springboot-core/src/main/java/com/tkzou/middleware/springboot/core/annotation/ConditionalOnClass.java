package com.tkzou.middleware.springboot.core.annotation;

import com.tkzou.middleware.springboot.core.condition.MyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当有当前clazz时才注入bean
 *
 * @author zoutongkun
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(MyCondition.class)
public @interface ConditionalOnClass {

    String value();
}
