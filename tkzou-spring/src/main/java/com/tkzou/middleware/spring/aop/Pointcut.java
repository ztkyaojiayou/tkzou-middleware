package com.tkzou.middleware.spring.aop;

/**
 * 定义一个切点接口，用于定义切点。
 *
 * @author zoutongkun
 */
public interface Pointcut {
    /**
     * 返回一个ClassFilter，用于匹配切点所应用的类。
     *
     * @return
     */
    ClassFilter getClassFilter();

    /**
     * 返回一个MethodMatcher，用于匹配切点所应用的方法。
     *
     * @return
     */
    MethodMatcher getMethodMatcher();
}
