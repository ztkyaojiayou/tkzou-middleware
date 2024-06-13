package com.tkzou.middleware.xxljobautoregister.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * job自动注册注解
 *
 * @author zoutongkun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlAutoRegister {
    /**
     * cron表达式
     *
     * @return
     */
    String cron();

    /**
     * job描述
     *
     * @return
     */
    String jobDesc() default "tkzou job";

    /**
     * job作者
     *
     * @return
     */
    String author() default "tkzou";

    /**
     * 执行策略
     * 默认为 ROUND 轮询方式
     * 可选： FIRST 第一个
     */
    String executorRouteStrategy() default "ROUND";

    /**
     * 调度状态，0为停止状态，1为运行状态
     *
     * @return
     */
    int triggerStatus() default 0;
}
