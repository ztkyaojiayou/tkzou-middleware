package com.tkzou.middleware.spring.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在Spring框架中注入外部属性值。
 * 通过AutowiredAnnotationBeanPostProcessor后置处理器处理，
 * 将@Value注解的属性值注入到对应的bean中。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Value {

    String value();
}