package com.tkzou.middleware.springframework.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

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

    /**
     * 当前目标对象的代理对象
     */
    protected Object proxy;
    /**
     * 目标对象的class对象
     */
    protected Class<?> targetClass;
    /**
     * 适用于当前类的当前方法的aop通知/拦截器链
     */
    protected List<Object> interceptorsAndDynamicMethodMatchers;
    /**
     * 当前执行的拦截器链的索引，注意就是控制执行顺序的！
     */
    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                                      Class<?> targetClass, List<Object> chain) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = chain;
    }

    /**
     * 执行目标方法
     * 但这里整合了拦截器的逻辑
     * 即需要执行完所有的拦截器链后，才执行目标方法！
     * 之前是在JdkDynamicAopProxy的invoke方法中实现的！
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object proceed() throws Throwable {
        // 初始currentInterceptorIndex为-1，每调用一次proceed就把currentInterceptorIndex+1
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            // 当调用次数 = 拦截器个数时
            // 触发当前method方法
            return method.invoke(target, arguments);
        }

        //执行当前的拦截器链
        //1.先获取拦截器，也即各种通知
        MethodInterceptor methodInterceptor =
                (MethodInterceptor) this.interceptorsAndDynamicMethodMatchers.get(currentInterceptorIndex);
        //2.索引+1，也即执行下一个拦截器
        currentInterceptorIndex++;
        //3.再执行该拦截器
        return methodInterceptor.invoke(this);
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
