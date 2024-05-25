package com.tkzou.middleware.spring.test.aop;

import com.tkzou.middleware.spring.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * aop动态代理加入bean创建逻辑测试
 * 也即在spring启动时，执行refresh时就会自动为现有代理的bean直接创建代理对象，
 * 且是一次性的，不放入ioc容器中！！！
 *
 * @author zoutongkun
 */
public class AutoProxyTest {

    @Test
    public void testAutoProxy() throws Exception {
        //读取配置文件，启动容器（也即执行refresh方法！！！）
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:auto-proxy" +
                ".xml");

        //获取代理对象
        WorldService worldService = applicationContext.getBean("worldService", WorldService.class);
        worldService.explode();
    }
}
