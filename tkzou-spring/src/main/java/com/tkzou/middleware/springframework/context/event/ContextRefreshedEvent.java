package com.tkzou.middleware.springframework.context.event;

import com.tkzou.middleware.springframework.context.ApplicationContext;

/**
 * 容器刷新事件，由ContextRefreshedEventListener监听
 *
 * @author zoutongkun
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {

	public ContextRefreshedEvent(ApplicationContext source) {
		super(source);
	}
}
