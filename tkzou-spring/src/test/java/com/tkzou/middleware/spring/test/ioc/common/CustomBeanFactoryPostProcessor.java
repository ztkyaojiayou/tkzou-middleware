package com.tkzou.middleware.spring.test.ioc.common;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.PropertyValues;
import com.tkzou.middleware.spring.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.BeanFactoryPostProcessor;

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

        BeanDefinition personBeanDefinition = beanFactory.getBeanDefinition("person");
        PropertyValues propertyValues = personBeanDefinition.getPropertyValues();
        //修改name属性值
        propertyValues.addPropertyValue(new PropertyValue("name", "new-person"));

    }
}
