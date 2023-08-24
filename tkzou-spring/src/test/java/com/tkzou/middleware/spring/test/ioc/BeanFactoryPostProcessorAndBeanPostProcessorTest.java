package com.tkzou.middleware.spring.test.ioc;

import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.xml.XmlBeanDefinitionReader;
import com.tkzou.middleware.spring.test.ioc.bean.Car;
import com.tkzou.middleware.spring.test.ioc.bean.Person;
import com.tkzou.middleware.spring.test.ioc.common.CustomBeanFactoryPostProcessor;
import com.tkzou.middleware.spring.test.ioc.common.CustomerBeanPostProcessor;
import org.junit.Test;

/**
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 19:01
 */
public class BeanFactoryPostProcessorAndBeanPostProcessorTest {

    @Test
    public void testBeanFactoryPostProcessor() throws Exception {
        //1.先加载和解析xml，完成所有BeanDefinition的加载
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        //此时就完成了xml的解析，同时完成了所有BeanDefinition的加载，但还没有实例化
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        //2.现在在实例化前搞点事情
        CustomBeanFactoryPostProcessor beanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        //3.再实例化，获取该bean
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
    }

    @Test
    public void testBeanPostProcessor() throws Exception {
        //1.先加载和解析xml，完成所有BeanDefinition的加载
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        //此时就完成了xml的解析，同时完成了所有BeanDefinition的加载，但还没有实例化
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");

        //2.添加bean实例化后的处理器
        CustomerBeanPostProcessor curBeanPostProcessor = new CustomerBeanPostProcessor();
        beanFactory.addBeanPostProcessor(curBeanPostProcessor);

        //3.实例化bean并获取该bean对象
        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
    }
}
