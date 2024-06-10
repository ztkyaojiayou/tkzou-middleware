package com.tkzou.middleware.dynamicdb.annotation;

import com.tkzou.middleware.dynamicdb.constant.DbConstant;
import com.tkzou.middleware.dynamicdb.handler.VariableDataSourceHandler;

import java.lang.annotation.*;

/**
 * 自定义多数据源切换注解
 * 优先级：先方法，后类，如果方法覆盖了类上的数据源类型，以方法的为准，否则以类上的为准
 * 使用时，将该注解打到service层或dao层的方法上即可，
 * 但要注意切换失败的情况，因为都是aop，因此和事务失效基本相同！
 * 可参考@transactional的使用方法和失效场景！
 * 考虑到分库分表，因此有必要加一个根据不同请求动态获取数据源的功能，
 * 因此使用一个专门的处理器来处理，参数可以是目标方法的参数，也可以是上下文的参数
 * 比如若是按照酒店id分库，则可以在每个方法上带上一个hotelId参数，也可以直接从上下文中获取！
 * 且通常就是从上下文中获取（那么就需要先将该参数使用threadLocal保存起来！）
 *
 * @Author: zoutongkun
 * @CreateDate: 2024/5/17 14:00
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    /**
     * 切换数据源名称，默认mysql_master
     *
     * @return
     */
    String value() default DbConstant.MYSQL_MASTER;

    /**
     * 用于获取当前请求的实际的数据源
     * 因为可能会分库分表，如根据酒店id分库
     * 那么每个酒店都会有主库和从库，同时还分生产环境和测试环境
     *
     * @return
     */
    Class<? extends VariableDataSourceHandler> handler() default VariableDataSourceHandler.Default.class;
}
