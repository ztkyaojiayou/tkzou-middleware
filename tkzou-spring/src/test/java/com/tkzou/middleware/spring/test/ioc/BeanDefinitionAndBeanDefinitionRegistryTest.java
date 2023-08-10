package com.tkzou.middleware.spring.test.ioc;

import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.spring.test.ioc.service.HelloSpringService;
import org.junit.Test;

/**
 * ioc容器测试
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 15:38
 */
public class BeanDefinitionAndBeanDefinitionRegistryTest {

    @Test
    public void testBeanFactory() {
        //1.构建目标对象的BeanDefinition
//        BeanDefinition beanDefinition = new BeanDefinition();
//        beanDefinition.setBeanClass(HelloSpringService.class);
        //方式2
        BeanDefinition beanDefinition02 = new BeanDefinition(HelloSpringService.class);
        //2.注册
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("helloSpringService", beanDefinition02);
        //3.获取
        HelloSpringService res = (HelloSpringService) beanFactory.getBean("helloSpringService");
        res.sayHello();
    }
}
