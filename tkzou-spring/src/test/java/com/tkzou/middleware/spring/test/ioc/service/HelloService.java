package com.tkzou.middleware.spring.test.ioc.service;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.BeanFactory;
import com.tkzou.middleware.spring.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.spring.context.ApplicationContext;
import com.tkzou.middleware.spring.context.ApplicationContextAware;

/**
 * @author derekyi
 * @date 2020/11/22
 */
public class HelloService implements ApplicationContextAware, BeanFactoryAware {

	private ApplicationContext applicationContext;

	private BeanFactory beanFactory;

	public String sayHello() {
		System.out.println("hello");
		return "hello";
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
