package com.tkzou.middleware.springframework.context.event;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.BeanFactory;
import com.tkzou.middleware.springframework.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.tkzou.middleware.springframework.context.ApplicationEvent;
import com.tkzou.middleware.springframework.context.ApplicationListener;

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
     * 保存所有的事件监听器，虽然是在父类中注册，
     * 但子类可以直接使用，因为这里使用了protected！
     * 相当于事件监听器注册中心
     */
    protected final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new HashSet<>();
    /**
     * ioc容器
     */
    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    private ConfigurableBeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans " +
                "because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
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
