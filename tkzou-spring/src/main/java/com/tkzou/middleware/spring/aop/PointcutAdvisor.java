package com.tkzou.middleware.spring.aop;

/**
 * 切点和通知的组合，用于定义AOP切面。
 * 即定义了在哪些方法上执行哪些通知。
 *
 * @author zoutongkun
 */
public interface PointcutAdvisor extends Advisor {
    /**
     * 获取切点
     *
     * @return
     */
    Pointcut getPointcut();
}
