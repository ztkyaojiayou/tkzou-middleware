package com.tkzou.middleware.spring.aop;

import java.lang.reflect.Method;

/**
 * 前置方法通知
 * 即在目标方法执行之前执行的逻辑
 * 该接口只负责写具体的逻辑，与执行解耦！
 * 执行的类在adapter包中定义！
 *
 * @author zoutongkun
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    /**
     * 前置通知逻辑
     * 由我们通过实现当前接口来定义
     * 但执行该逻辑则专门定义一个实现了MethodInterceptor接口的类去做！
     * 参考：测试类中的WorldServiceInterceptor
     * 这里把目标方法的参数都传递给我们了，我们在自定义逻辑时就可以直接使用它们啦！
     *
     * @param method
     * @param args
     * @param target
     * @throws Throwable
     */
    void before(Method method, Object[] args, Object target) throws Throwable;
}
