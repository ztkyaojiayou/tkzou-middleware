package com.tkzou.middleware.springframework.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * 封装aop的核心逻辑，包括目标对象、方法增强拦截器（包括目标方法快照信想和诸如前置/后置通知等增强逻辑）、切入点等。
 * 核心类
 *
 * @author zoutongkun
 */
public class AdvisedSupport {
    /**
     * 是否使用cglib代理，用于区分使用哪种代理方式！
     * 这里为false，易知默认是使用jdk动态代理的！
     * 更新：改为了true
     */
    private boolean proxyTargetClass = true;

    /**
     * 目标对象
     */
    private TargetSource targetSource;
    /**
     * 用于拦截目标方法的接口
     * 目标方法的快照信息则是封装在MethodInvocation中，
     * 在invoke方法中会做两件事：
     * 1.执行MethodInvocation中的proceed方法
     * 2.在它的前后还可以执行自定义的逻辑，具体而言，只需自己实现该接口！
     * 这就是aop的效果！
     * 也即执行前后也即在目标方法执行前后添加自定义的逻辑啦！！！
     * 这就是aop的核心思想，也即对方法进行拦截，在方法执行前后添加自定义的逻辑
     */
    private MethodInterceptor methodInterceptor;
    /**
     * 切入点方法匹配器
     * 只有符合该匹配规则的方法才会被拦截，这是最大的前提！
     */
    private MethodMatcher methodMatcher;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
}
