package com.tkzou.middleware.springframework.context.event;

import com.tkzou.middleware.springframework.context.ApplicationContext;
import com.tkzou.middleware.springframework.context.ApplicationEvent;

/**
 * ApplicationContextEvent是ApplicationEvent的一个子类，用于表示Spring应用程序上下文事件。
 *
 * @author zoutongkun
 */
public abstract class ApplicationContextEvent extends ApplicationEvent {

    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
