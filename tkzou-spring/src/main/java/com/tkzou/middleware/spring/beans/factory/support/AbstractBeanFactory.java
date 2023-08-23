package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.ConfigurableBeanFactory;
import org.apache.commons.lang3.ObjectUtils;

/**
 * bean抽象工厂
 * 作用：
 * 1.具备获取bean对象
 * 1.1对于一般bean的获取，实现BeanFactory
 * 1.2对于单例bean的获取，继承DefaultSingletonBeanRegistry（它会实现SingletonBeanRegistry接口）
 * 2.同时需要注册bean对应的BeanDefinition，由于是抽象接口，因此这里先作第定义
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:29
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        //又是链式调用
        return ((T) this.getBean(name));
    }

    /**
     * 这里获取单例bean对象
     * 逻辑：
     * 先从容器中获取，若没有则先将该bean注册进容器同时返回
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object getBean(String beanName) throws BeansException {
        //1.先直接去bean工厂/容器中获取该bean对象
        //这里我们默认获取的是单例对象
        Object singleton = super.getSingleton(beanName);
        if (ObjectUtils.isNotEmpty(singleton)) {
            return singleton;
        } else {
            //通过反射生成对象，同时注册进容器
            BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
            return createBean(beanName, beanDefinition);
        }
    }

    /**
     * 根据beanName和BeanDefinition生成对象
     * 也就是根据class对象和反射生成对象实例
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据bean名称获取BeanDefinition，也即获取对应的class对象
     * 用于根据反射生成bean对象
     *
     * @param beanName
     * @return
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName);
}
