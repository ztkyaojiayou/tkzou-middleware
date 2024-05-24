package com.tkzou.middleware.spring.aop;

/**
 * 被代理对象的封装
 * 源码中其实是一个接口，这里为了方便理解，写成类的形式
 * 理解为一个及具体的实现即可
 *
 * @author zoutongkun
 */
public class TargetSource {
    /**
     * 被代理对象，也即原对象
     */
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    public Class<?>[] getTargetClass() {
        // 获取被代理对象所实现的所有接口
        return this.target.getClass().getInterfaces();
    }

    public Object getTarget() {
        return target;
    }
}
