package com.tkzou.middleware.test.ioc;

/**
 * 用于测试的类
 * 无成员变量，有默认的无参构造函数，
 * 这是重点，这一版就是根据这个无参构造函数创建的实例
 *
 * 关于有状态的Bean与无状态的Bean
 * 1.有状态的bean，具有数据存储功能。
 * 无状态的bean，只有普通的对数据的操作方法，而没有存储功能。
 * 2.有状态的bean不应该被线程共享，无状态的bean可以被线程共享。
 * 3.有状态的Bean，多线程环境下不安全，那么适合用Prototype原型模式
 * （当然，由于spring使用了ThreadLocal进行多线程处理，绝大多数bean都可以声明为singleton作用域。这是后话）
 * 无状态的Bean适合单例模式（singleton），这样可以共享实例，提高性能。
 * 4.但如Service层、Dao层用默认singleton就行，虽然Service类也有dao这样的属性，
 * 但dao这些类都是没有状态信息的，也就是相当于不变(immutable)类，所以不影响。
 *
 * 参考：https://blog.csdn.net/jing12062011/article/details/77185629
 * https://www.jb51.net/article/235203.htm
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
