package com.tkzou.middleware.springframework.test.ioc.common;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValue;
import com.tkzou.middleware.springframework.beans.PropertyValues;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * 自定义实现类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 18:53
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //对person对象的beanDefinition进行修改！
        BeanDefinition personBeanDefinition = beanFactory.getBeanDefinition("person");
        PropertyValues propertyValues = personBeanDefinition.getPropertyValues();
        //修改name属性值
        propertyValues.addPropertyValue(new PropertyValue("name", "new-person"));

    }
}
