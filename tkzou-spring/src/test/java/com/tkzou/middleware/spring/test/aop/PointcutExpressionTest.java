package com.tkzou.middleware.spring.test.aop;

import com.tkzou.middleware.spring.aop.aspectj.AspectJExpressionPointcut;
import com.tkzou.middleware.spring.test.ioc.service.HelloService;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author derekyi
 * @date 2020/12/5
 */
public class PointcutExpressionTest {

	@Test
	public void testPointcutExpression() throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* org.springframework.test.service.HelloService.*(..))");
		Class<HelloService> clazz = HelloService.class;
		Method method = clazz.getDeclaredMethod("sayHello");
		Assertions.assertThat(pointcut.matches(clazz)).isTrue();
		Assertions.assertThat(pointcut.matches(method, clazz)).isTrue();
	}
}
