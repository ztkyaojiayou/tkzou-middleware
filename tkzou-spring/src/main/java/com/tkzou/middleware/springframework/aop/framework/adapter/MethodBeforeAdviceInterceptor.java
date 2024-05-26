package com.tkzou.middleware.springframework.aop.framework.adapter;


import com.tkzou.middleware.springframework.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 前置通知逻辑处理器
 * 里面会集成前置通知类
 * 本质是个增强方法的拦截器！
 * 参考测试类中的WorldServiceInterceptor
 *
 * @author :zoutongkun
 * @date :2024/5/24 11:19 下午
 * @description :
 * @modyified By:
 */
public class MethodBeforeAdviceInterceptor implements MethodInterceptor {
    /**
     * 前置通知逻辑类
     */
    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    /**
     * 执行目标方法+前置通知逻辑
     *
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //先执行前置通知逻辑
        this.advice.before(methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getThis());
        //再执行目标方法
        return methodInvocation.proceed();
    }
}
