package com.tkzou.middleware.spring.context.event;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.BeanFactory;
import com.tkzou.middleware.spring.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.spring.context.ApplicationEvent;
import com.tkzou.middleware.spring.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * 事件发布者抽象类
 * 同时实现了{@link BeanFactoryAware}接口，用于将容器对象注入进来！
 *
 * @author zoutongkun
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {
    /**
     * 保存所有的事件监听器，虽然是在父类中注册，但子类可以直接使用，因为这里使用了protected！
     * 相当于事件监听器注册中心
     */
    protected final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new HashSet<>();

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        //强转一下
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }
}
