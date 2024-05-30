package com.tkzou.middleware.springframework.aop.framework.adapter;

import com.tkzou.middleware.springframework.aop.AfterAdvice;
import com.tkzou.middleware.springframework.aop.AfterReturningAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * 后置增强拦截器
 *
 * @author :zoutongkun
 * @date :2024/5/30 10:19 下午
 * @description :
 * @modyified By:
 */
public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    private AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor() {
    }

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //先执行目标方法
        Object retVal = mi.proceed();
        //再执行后置通知
        this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }
}
