package com.tkzou.middleware.spring.support;

import com.tkzou.middleware.spring.BeansException;
import com.tkzou.middleware.spring.config.BeanDefinition;

/**
 * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象工厂类
 * 这是个抽象类，父类的getBeanDefinition方法交由子类实现
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 15:02
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象
     * 1.根据class对象，利用反射，生成bean对象
     * 2.将该对象与beanName绑定，并存入bean容器中！！！
     * 3.同时，返回该bean对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        return doCreateBean(beanName, beanDefinition);
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        //1.获取class对象
        Class beanClass = beanDefinition.getBeanClass();
        Object bean;
        try {
            //2.根据反射生成bean对象
            //todo 易知这里只适⽤于bean有⽆参构造函数的情况
            bean = beanClass.newInstance();
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        //3.将该beanName和生成的bean对象绑定，并存入bean容器中！！！
        addSingleton(beanName, bean);

        //4.同时返回该生成的bean对象
        return bean;
    }
}
