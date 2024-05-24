package com.tkzou.middleware.spring.context.event;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.BeanFactory;
import com.tkzou.middleware.spring.context.ApplicationEvent;
import com.tkzou.middleware.spring.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 事件发布者默认实现
 *
 * @author zoutongkun
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    /**
     * 为什么还需要自己传beanFactory？
     * 因为spring的refresh方法中，在初始化当前类时还没有初始化ioc容器，因此需要自己手动先初始化！
     *
     * @param beanFactory
     */
    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        //遍历所有监听者
        for (ApplicationListener<ApplicationEvent> applicationListener : applicationListeners) {
            //判断当前监听者是否监听了该事件
            if (supportsEvent(applicationListener, event)) {
                //由监听者执行自己的处理逻辑
                applicationListener.onApplicationEvent(event);
            }
        }
    }

    /**
     * 判断事件监听者是否监听了该事件
     *
     * @param applicationListener
     * @param event
     * @return
     */
    private boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
        //获取到当前监听者所监听的具体事件类型，其实就是获取T的具体类型，属于类的泛型参数的获取。
        Type type = applicationListener.getClass().getGenericInterfaces()[0];
        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        //监听的事件类型/泛型参数的全类名
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClassName;
        try {
            //转为Class对象
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("wrong event class name: " + className);
        }
        //判断一下是否和当前事件类型属于同一个类或者是其父类，若是则表示支持
        return eventClassName.isAssignableFrom(event.getClass());
    }
}
