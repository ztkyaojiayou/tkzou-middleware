package com.tkzou.middleware.dynamicdb.annotation;

import com.tkzou.middleware.dynamicdb.constant.DbConstant;

import java.lang.annotation.*;

/**
 * 自定义多数据源切换注解
 * 优先级：先方法，后类，如果方法覆盖了类上的数据源类型，以方法的为准，否则以类上的为准
 * 使用时，将该注解打到service层或dao层的方法上即可，
 * 但要注意切换失败的情况，因为都是aop，因此和事务失效基本相同！
 * 可参考@transactional的使用方法和失效场景！
 *
 * @Author: zoutongkun
 * @CreateDate: 2024/5/17 14:00
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    //切换数据源名称，默认mysql_master
    String value() default DbConstant.MYSQL_MASTER;
}
