package com.tkzou.middleware.springframework.aop.framework;

import com.tkzou.middleware.springframework.aop.AdvisedSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * aop通知链工厂
 * 即创建多个aop通知
 *
 * @author zoutongkun
 */
public interface AdvisorChainFactory {
    /**
     * 获取aop通知链
     *
     * @param config      用于获取所有的aop通知
     * @param method      指定方法
     * @param targetClass 指定类
     * @return
     */
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method,
                                                             Class<?> targetClass);
}
