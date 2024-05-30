package com.tkzou.middleware.springframework.aop.framework.adapter;


import com.tkzou.middleware.springframework.aop.BeforeAdvice;
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
public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice {
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
     * @param mi
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //先执行前置通知逻辑
        this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        //再执行目标方法
        return mi.proceed();
    }
}
