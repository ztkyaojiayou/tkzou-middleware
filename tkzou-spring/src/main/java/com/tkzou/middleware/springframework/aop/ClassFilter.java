package com.tkzou.middleware.springframework.aop;

/**
 * 类匹配器
 *
 * @author zoutongkun
 */
public interface ClassFilter {
    /**
     * 当前类是否匹配目标切入点表达式
     *
     * @param clazz
     * @return
     */
    boolean matches(Class<?> clazz);
}
