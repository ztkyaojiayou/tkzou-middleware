package com.tkzou.middleware.spring.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 后置通知
 * 方法正常执行完之后执行，也即在try语句块中执行的逻辑
 *
 * @author zoutongkun
 */
public interface AfterReturningAdvice extends AfterAdvice {
    /**
     * 后置通知逻辑
     *
     * @param returnValue
     * @param method
     * @param args
     * @param target
     * @throws Throwable
     */
    void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}