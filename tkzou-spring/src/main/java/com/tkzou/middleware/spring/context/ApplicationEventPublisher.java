package com.tkzou.middleware.spring.context;

/**
 * 事件发布者接口，用于定义事件发布的方法。
 * ioc容器对象本身就实现了该接口，因此它可以直接调用该接口的方法来发布事件。
 * 具体是在AbstractApplicationContext中实现了该接口，并实现了publishEvent方法。
 *
 * @author zoutongkun
 */
public interface ApplicationEventPublisher {
    /**
     * 发布事件
     *
     * @param event
     */
    void publishEvent(ApplicationEvent event);
}
