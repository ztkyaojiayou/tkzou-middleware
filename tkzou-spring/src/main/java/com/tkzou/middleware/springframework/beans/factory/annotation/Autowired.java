package com.tkzou.middleware.springframework.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注入属性值，先按类型注入，若有多个时，则再按属性名称注入。
 * 通过AutowiredAnnotationBeanPostProcessor后置处理器处理，
 * 将@Autowired注解的属性值注入到对应的bean中。
 * 可以作用在字段、方法、方法参数上，但目前只处理了作用在字段上的情况
 * 一般还搭配Qualifier使用，此时就只注入该名称的bean了！
 *
 * @author zoutongkun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface Autowired {

}