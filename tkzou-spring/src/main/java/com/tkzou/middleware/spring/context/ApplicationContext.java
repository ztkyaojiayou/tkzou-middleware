package com.tkzou.middleware.spring.context;

import com.tkzou.middleware.spring.beans.factory.HierarchicalBeanFactory;
import com.tkzou.middleware.spring.beans.factory.ListableBeanFactory;
import com.tkzou.middleware.spring.core.io.ResourceLoader;

/**
 * 应用上下文接口
 * 同时还是一个事件发布者，
 * 因为实现了ApplicationEventPublisher
 *
 * @author zoutongkun
 */
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {
}
