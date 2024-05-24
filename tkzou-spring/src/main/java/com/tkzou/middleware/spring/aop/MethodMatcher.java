package com.tkzou.middleware.spring.aop;

import java.lang.reflect.Method;

/**
 * 方法匹配器
 * 要注意的是，对于切入点表达式的解析我们自己不实现，也不是重点，
 * 有专门的第三方库实现，即aspectjweaver。
 *
 * @author zoutongkun
 */
public interface MethodMatcher {
    /**
     * 判断当前方法是否匹配目标切入点表达式
     *
     * @param method
     * @param targetClass
     * @return
     */
    boolean matches(Method method, Class<?> targetClass);
}
