package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import com.tkzou.middleware.springframework.test.ioc.bean.Car;
import com.tkzou.middleware.springframework.test.ioc.bean.Person;
import org.junit.Test;

/**
 * xxl解析并注册bean测试
 * @author :zoutongkun
 * @date :2023/8/23 10:02 下午
 * @description :
 * @modyified By:
 */
public class XmlFileDefineBeanTest {
    @Test
    public void testXmlFile() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");

        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);

        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
    }
}
