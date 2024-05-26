package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.context.support.ClassPathXmlApplicationContext;
import com.tkzou.middleware.springframework.test.ioc.bean.Car;
import com.tkzou.middleware.springframework.test.ioc.bean.Person;
import org.junit.Test;

/**
 * ApplicationContext上下文测试
 *
 * @author :zoutongkun
 * @date :2023/8/29 9:44 下午
 * @description :
 * @modyified By:
 */
public class ApplicationContextTest {

    @Test
    public void testApplicationContext() {
        //new xml上下文，同时会执行refresh方法！！！
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath" +
                ":spring.xml");
        Person person = applicationContext.getBean("person", Person.class);
        System.out.println(person);
//因此此时取getBean时，因为ioc容器已经初始化了，因此会直接从ioc容器中获取bean！！！
        Car car = applicationContext.getBean("car", Car.class);
        System.out.println(car);
    }
}
