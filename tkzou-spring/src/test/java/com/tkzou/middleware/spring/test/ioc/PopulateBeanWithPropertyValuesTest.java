package com.tkzou.middleware.spring.test.ioc;

import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.PropertyValues;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.spring.test.ioc.bean.Person;
import org.junit.Test;

/**
 * 属性注入测试
 * 目前都是手动注册来测试，这其实是入口，后续就可以通过注解的方式获取对应的入参啦！！！
 * 主要是掌握原理
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 14:01
 */
public class PopulateBeanWithPropertyValuesTest {
    @Test
    public void testPopulateBeanWithPropertyValues() {
        //1.先给person类的属性赋值
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "tkzou"));
        //注意：参数值要和类型匹配，因为这里定义的是Object，务必按照目标类的实际类型传入，否则报参数不匹配错！！！
        // 即argument type mismatch
        propertyValues.addPropertyValue(new PropertyValue("age", 28));
        BeanDefinition personBeanDefinition = new BeanDefinition(Person.class, propertyValues);

        //2.再将其注册到beanDefinitionFactory中（手动）
        DefaultListableBeanFactory beanDefinitionFactory = new DefaultListableBeanFactory();
        beanDefinitionFactory.registerBeanDefinition("person", personBeanDefinition);

        //3.此时即可以去工厂获取这个bean啦！！！
        Person person = (Person) beanDefinitionFactory.getBean("person");
        System.out.println(person);
    }

}
