package com.tkzou.middleware.spring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * 按照属性名称注入bean
 * 需要配和@Autowired一起使用
 *
 * @author zoutongkun
 * @date 2024/5/26 12:40 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Inherited
@Documented
public @interface Qualifier {
    /**
     * 指定的bean名称
     *
     * @return
     */
    String value() default "";

}