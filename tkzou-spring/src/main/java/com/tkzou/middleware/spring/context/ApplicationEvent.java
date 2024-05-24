package com.tkzou.middleware.spring.context;

import java.util.EventObject;

/**
 * spring 事件
 * 抽象类
 *
 * @author zoutongkun
 */
public abstract class ApplicationEvent extends EventObject {
    /**
     * 创建一个新的事件
     *
     * @param source
     */
    public ApplicationEvent(Object source) {
        super(source);
    }
}
