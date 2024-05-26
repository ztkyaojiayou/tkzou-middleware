package com.tkzou.middleware.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * 对Advice的封装
 * 也就是aop通知，也就是各种增强逻辑的总接口
 *
 * @author zoutongkun
 */
public interface Advisor {
    /**
     * 获取通知
     *
     * @return
     */
    Advice getAdvice();

}
