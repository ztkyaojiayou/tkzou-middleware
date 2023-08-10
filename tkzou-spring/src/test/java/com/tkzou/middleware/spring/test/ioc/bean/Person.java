package com.tkzou.middleware.spring.test.ioc.bean;

/**
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 13:59
 */
public class Person {

    private String name;

    private int age;

    /**
     * 新增引用类
     * 此时，在注入的时候我们会先去容器中以该名称为beanName查找对应的bean来注入，
     * 若没有，则先创建该bean并注入，同时存入ioc容器！！！
     * 为了简单起见，这里暂先不考虑循环依赖的问题！！！
     */
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

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", car=" + car +
                '}';
    }
}
