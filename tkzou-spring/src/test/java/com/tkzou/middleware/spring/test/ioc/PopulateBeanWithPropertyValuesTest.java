package com.tkzou.middleware.spring.test.ioc;

import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.PropertyValues;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.BeanReference;
import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.spring.test.ioc.bean.Car;
import com.tkzou.middleware.spring.test.ioc.bean.Person;
import org.junit.Test;

/**
 * 属性注入测试
 * 目前都是手动注册来测试，这其实是入口，后续就可以通过注解的方式获取对应的入参啦！！！
 * 主要是掌握原理~
 * 注意：在测试时，需要放开applyPropertyValues方法，否则无法测试！！！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 14:01
 */
public class PopulateBeanWithPropertyValuesTest {

    /**
     * 为目标bean注入一般属性
     */
    @Test
    public void testPopulateBeanWithPropertyValues() {
        /**
         * 1.先将目标bean的BeanDefinition手动注入
         */
        //1.1先给person类的属性赋值
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "tkzou"));
        //注意：参数值要和类型匹配，因为这里定义的是Object，务必按照目标类的实际类型传入，否则报参数不匹配错！！！
        // 即argument type mismatch
        propertyValues.addPropertyValue(new PropertyValue("age", 28));
        BeanDefinition personBeanDefinition = new BeanDefinition(Person.class, propertyValues);

        //1.2再将其注册到beanDefinitionFactory中（手动）
        DefaultListableBeanFactory beanDefinitionFactory = new DefaultListableBeanFactory();
        beanDefinitionFactory.registerBeanDefinition("person", personBeanDefinition);

        /**
         * 2.再就可以获取bean了
         * 逻辑：若容器中有该bean，则直接获取，若没有，会根据目标bean的BeanDefinition创建bean！！！
         */
        //3.此时即可以去工厂获取这个bean啦！！！
        //todo 获取bean这个方法才是关键！！！
        Person person = (Person) beanDefinitionFactory.getBean("person");
        System.out.println(person);
    }

    /**
     * 为目标bean注入另一个bean
     * 逻辑与上相同，也是先手动注册目标bean的BeanDefinition，后再获取/创建bean！
     */
    @Test
    public void testPopulateBeanWithBean() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        /**
         * 1.先将目标bean的BeanDefinition手动注入
         * 这里涉及Person和Car这两个类
         */
        //1.1注册Car实例
        PropertyValues propertyValuesForCar = new PropertyValues();
        propertyValuesForCar.addPropertyValue(new PropertyValue("brand", "benz"));
        BeanDefinition carBeanDefinition = new BeanDefinition(Car.class, propertyValuesForCar);
        beanFactory.registerBeanDefinition("car", carBeanDefinition);

        //1.2注册Person实例
        PropertyValues propertyValuesForPerson = new PropertyValues();
        propertyValuesForPerson.addPropertyValue(new PropertyValue("name", "tkzou"));
        propertyValuesForPerson.addPropertyValue(new PropertyValue("age", 28));
        //1.2.1Person实例依赖Car实例
        propertyValuesForPerson.addPropertyValue(new PropertyValue("car", new BeanReference("car")));
        BeanDefinition beanDefinition = new BeanDefinition(Person.class, propertyValuesForPerson);
        beanFactory.registerBeanDefinition("person", beanDefinition);

        /**
         * 2.再获取/创建bean
         */
        //2.获取该bean以验证
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
    }
}
