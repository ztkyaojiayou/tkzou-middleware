package com.tkzou.middleware.mybatis.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> 标识插件作用的方法 </p>
 * 用于指定拦截器要拦截的方法，
 * 而非全部方法都拦截，做到精准控制！务必掌握！
 * 定义在拦截器上才有效！
 * 比如：比如对于分页插件，我们只需要对查询方法分页，其他增删改操作是不需要分页的！
 * 就相当于spring-aop中的切点！！！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Signature {
    /**
     * 要拦截的类，比如StatementHandler，
     * 而非拦截器或插件本身，插件只是之前增强逻辑中的一部分而已,比如LimitInterceptor！！！
     *
     * @return
     */
    Class<?> type();

    /**
     * 要拦截的方法名称，通过发反射获取对应的method对象！
     *
     * @return
     */
    String method();

    /**
     * 要拦截的方法参数的clazz，目的是确定唯一的方法，因为方法可能有重载！
     */
    Class<?>[] args();

}
