package com.tkzou.middleware.springframework.test.ioc.bean;

import com.tkzou.middleware.springframework.beans.factory.DisposableBean;
import com.tkzou.middleware.springframework.beans.factory.InitializingBean;
import com.tkzou.middleware.springframework.beans.factory.annotation.Autowired;
import com.tkzou.middleware.springframework.beans.factory.annotation.Qualifier;

/**
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 13:59
 */
public class Person implements InitializingBean, DisposableBean {

    private String name;

    private int age;

    /**
     * 新增引用类
     * 1.若只有@Autowired时，则先按type，再按name注入
     * 1.若还有@Qualifier时，则先按type，再按该注解指定的beanName注入
     * 此时，spring会先去容器中以该type+该beanName查找对应的bean来注入
     * 为了简单起见，这里暂先不考虑循环依赖的问题！！！
     */
    @Autowired
    @Qualifier("car")
    private Car car;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * 初始化方法
     * 此时已经完成了bean的实例化和属性注入
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("I was born in the method named afterPropertiesSet");
    }

    /**
     * 销毁方法
     * ioc容器关闭/项目关闭时调用
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("I died in the method named destroy");
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", car=" + car +
                '}';
    }
}
