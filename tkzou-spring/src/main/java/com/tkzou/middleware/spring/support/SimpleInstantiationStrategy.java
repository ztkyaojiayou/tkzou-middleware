package com.tkzou.middleware.spring.support;

import com.tkzou.middleware.spring.BeansException;
import com.tkzou.middleware.spring.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 简单的bean实例化策略（默认）
 * 即根据目标类的无参构造函数实例化对象
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 17:09
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition) {
        Class beanClass = beanDefinition.getBeanClass();
        try {
            //使用无参构造器构造对象实例（默认）
            //关于getDeclaredConstructor，参考：https://blog.csdn.net/weixin_49116772/article/details/131170855
            Constructor declaredConstructor = beanClass.getDeclaredConstructor();
            return declaredConstructor.newInstance();
        } catch (Exception e) {
            throw new BeansException("Failed to instantiate [" + beanClass.getName() + "]", e);
        }
    }
}
