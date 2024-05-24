package com.tkzou.middleware.spring.aop.framework;

/**
 * aop代理对象接口
 *
 * @author zoutongkun
 */
public interface AopProxy {
    /**
     * 获取代理对象
     *
     * @return
     */
    Object getProxy();
}
