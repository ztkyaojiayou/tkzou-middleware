package com.tkzou.middleware.springframework.context.event;

import com.tkzou.middleware.springframework.context.ApplicationEvent;
import com.tkzou.middleware.springframework.context.ApplicationListener;

/**
 * spring 事件发布者
 * 核心接口，本质就是用于管理事件监听器的，相当于事件监听器的注册中心！
 * 属于订阅-发布模式
 *
 * @author zoutongkun
 */
public interface ApplicationEventMulticaster {
    /**
     * 添加一个事件监听器
     *
     * @param listener
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 删除一个事件监听器
     *
     * @param listener
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 事件广播/发布事件
     * 即把目标事件广播给所有监听了该事件的事件监听器
     * 核心逻辑就是调用一下监听了这个事件的所有监听器的onApplicationEvent方法！！！
     * 这是事件监听机制中的核心方法
     *
     * @param event
     */
    void multicastEvent(ApplicationEvent event);
}
