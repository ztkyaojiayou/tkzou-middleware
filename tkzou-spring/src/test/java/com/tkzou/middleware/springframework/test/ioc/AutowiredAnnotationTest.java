package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.context.support.ClassPathXmlApplicationContext;
import com.tkzou.middleware.springframework.test.ioc.bean.Person;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * 测试@Autowired注解
 * @author zoutongkun
 * @description: TODO
 * @date 2024/5/26 12:27
 */
public class AutowiredAnnotationTest {

	@Test
	public void testAutowiredAnnotation() throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:autowired-annotation.xml");

		Person person = applicationContext.getBean(Person.class);
		Assertions.assertThat(person.getCar()).isNotNull();
	}
}
