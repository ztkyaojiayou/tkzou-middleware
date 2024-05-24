package com.tkzou.middleware.spring.aop.framework;

import com.tkzou.middleware.spring.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理
 * 该类属于代理对象工厂+代理增强逻辑二合一！
 * 一般而言，可以单独定义一个代理对象工厂来创建代理对象，参考mybatis。
 *
 * @author derekyi
 * @date 2020/12/5
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    /**
     * aop的核心信息
     */
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * 创建代理对象
     * 目标对象在AdvisedSupport中
     *
     * @return
     */
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), advised.getTargetSource().getTargetClass(), this);
    }

    /**
     * 代理增强逻辑
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //先需要判断当前方法是否需要拦截，也即是否需要增强，判断方式就是切入点表达式！！！
        //1.符合切入点表达式的，需要增强
        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
            //代理方法
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            //执行方法，包括原方法的执行和自定义的增强逻辑的执行，里面就可能包括类似于前置通知或后置通知的逻辑！！！
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
                    method, args));
        }
        //2.不符合切入点表达式的，不需要增强，直接执行原方法
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }
}
