package com.tkzou.middleware.springframework.context;

import com.tkzou.middleware.springframework.beans.factory.HierarchicalBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.ListableBeanFactory;
import com.tkzou.middleware.springframework.core.io.ResourceLoader;

/**
 * 应用上下文接口
 * 同时还是一个事件发布者，
 * 因为实现了ApplicationEventPublisher
 * ApplicationEventMulticaster接口是注册监听器和发布事件的抽象接口，
 * AbstractApplicationContext包含其实现类实例作为其属性，
 * 这就使得ApplicationContext容器具有注册监听器和发布事件的能⼒啦！
 *
 * @author zoutongkun
 */
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {
}
