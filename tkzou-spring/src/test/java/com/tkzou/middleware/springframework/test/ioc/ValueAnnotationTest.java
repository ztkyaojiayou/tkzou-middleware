package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.context.support.ClassPathXmlApplicationContext;
import com.tkzou.middleware.springframework.test.ioc.bean.Car;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * 测试@Value注解
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2024/5/26 12:27
 */
public class ValueAnnotationTest {

    @Test
    public void testValueAnnotation() throws Exception {
        //加载配置文件，初始化ioc容器
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:value" +
                "-annotation.xml");
        //获取指定bean
        Car car = applicationContext.getBean("car", Car.class);
        Assertions.assertThat(car.getBrand()).isEqualTo("lamborghini");
    }
}
