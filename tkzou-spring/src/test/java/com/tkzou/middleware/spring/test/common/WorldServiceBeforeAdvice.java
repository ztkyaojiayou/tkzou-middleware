package com.tkzou.middleware.spring.test.common;

import com.tkzou.middleware.spring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * 具体的前置通知
 *
 * @author zoutongkun
 */
public class WorldServiceBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) {
        System.out.println("前置通知逻辑执行啦！");
    }
}
