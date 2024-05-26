package com.tkzou.middleware.springframework.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注入外部属性值。
 * 通过AutowiredAnnotationBeanPostProcessor后置处理器处理，
 * 将@Value注解的属性值注入到对应的bean中。
 * 可以作用在字段、方法、方法参数上，但目前只处理了作用在字段上的情况
 * @author zoutongkun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Value {

    String value();
}