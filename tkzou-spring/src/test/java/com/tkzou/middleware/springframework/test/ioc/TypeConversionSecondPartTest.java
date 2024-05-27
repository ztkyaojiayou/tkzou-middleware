package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.context.support.ClassPathXmlApplicationContext;
import com.tkzou.middleware.springframework.test.ioc.bean.Car;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;

/**
 * 类型转换器整合进spring生命周期测试
 *
 * @author zoutongkun
 */
public class TypeConversionSecondPartTest {

    @Test
    public void testConversionService() throws Exception {
        //加载配置文件，创建Spring容器
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:type-conversion-second-part.xml");

        Car car = applicationContext.getBean("car", Car.class);
        Assertions.assertThat(car.getPrice()).isEqualTo(1000000);
        Assertions.assertThat(car.getProduceDate()).isEqualTo(LocalDate.of(2021, 1, 1));
    }
}
