package com.tkzou.middleware.spring.test.expanding;

import com.tkzou.middleware.spring.context.support.ClassPathXmlApplicationContext;
import com.tkzou.middleware.spring.test.ioc.bean.Car;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * 使用属性值替换xml中的占位符测试
 */
public class PropertyPlaceholderConfigurerTest {

    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:property" +
                "-placeholder-configurer.xml");
        Car car = applicationContext.getBean("car", Car.class);
        Assertions.assertThat(car.getBrand()).isEqualTo("lamborghini");
    }
}
