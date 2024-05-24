package com.tkzou.middleware.spring.context;

import java.util.EventListener;

/**
 * 事件监听者
 * 监听的事件类型为T，T继承自ApplicationEvent
 * 我们可以通过getGenericInterfaces()方法来获取到T的具体类型，
 * 其实就是获取泛型参数类型的方法，之前讲过！
 *
 * @author zoutongkun
 */
public interface ApplicationListener<T extends ApplicationEvent> extends EventListener {
    /**
     * 处理事件
     *
     * @param event
     */
    void onApplicationEvent(T event);
}
