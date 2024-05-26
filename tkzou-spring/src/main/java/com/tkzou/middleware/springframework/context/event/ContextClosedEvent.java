package com.tkzou.middleware.springframework.context.event;

import com.tkzou.middleware.springframework.context.ApplicationContext;

/**
 * 容器关闭事件，由ContextClosedEventListener监听
 *
 * @author zoutongkun
 */
public class ContextClosedEvent extends ApplicationContextEvent {

	public ContextClosedEvent(ApplicationContext source) {
		super(source);
	}
}
