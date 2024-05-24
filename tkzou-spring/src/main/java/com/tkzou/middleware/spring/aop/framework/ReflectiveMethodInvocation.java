package com.tkzou.middleware.spring.aop.framework;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * 目标方法快照信息封装/保存，以便更灵活地执行该方法！
 * 唯一确定一个方法，并且可以直接执行该方法！
 * 在mybatis的Plugin/插件逻辑和rpc方法调用逻辑中也有使用！！！
 *
 * @author zoutongkun
 */
public class ReflectiveMethodInvocation implements MethodInvocation {
    /**
     * 目标对象
     */
    public final Object target;
    /**
     * 目标方法
     */
    public final Method method;
    /**
     * 当前方法执行时的参数
     */
    public final Object[] arguments;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }

    /**
     * 执行目标方法
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object proceed() throws Throwable {
        return method.invoke(target, arguments);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }
}
