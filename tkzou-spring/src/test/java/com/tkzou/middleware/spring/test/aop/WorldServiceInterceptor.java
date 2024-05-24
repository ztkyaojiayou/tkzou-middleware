package com.tkzou.middleware.spring.test.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 相当于就是一个切面！
 *
 * @author zoutongkun
 */
public class WorldServiceInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) {
        //在目标方法执行前执行的逻辑，可以理解为前置通知！
        before("前置通知执行了-----------------------");
        //执行目标方法
        Object result = null;
        try {
            result = invocation.proceed();
        } catch (Throwable e) {
            afterThrowing("异常通知执行了-----------------------" + e.getMessage());
        }
        //在目标方法执行完后执行的逻辑，可以理解为后置通知！
        afterReturn("后置通知执行了-----------------------");
        return result;
    }

    /**
     * 异常通知
     *
     * @param s
     */
    private void afterThrowing(String s) {
        System.out.println(s);
    }

    /**
     * 前置通知
     *
     * @param s
     */
    private void afterReturn(String s) {
        System.out.println(s);
    }

    /**
     * 后置通知
     *
     * @param s
     */
    private void before(String s) {
        System.out.println(s);
    }
}
