package com.tkzou.middleware.test.ioc;

/**
 * 用于测试的类
 * 无成员变量，有默认的无参构造函数，
 * 这是重点，这一版就是根据这个无参构造函数创建的实例
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 15:37
 */
public class HelloSpringService {

    public String sayHello() {
        System.out.println("hello,your spring-IOC success~~~~~~~~~~~~~~~~~~~");
        return "your spring-IOC success!";
    }
}
