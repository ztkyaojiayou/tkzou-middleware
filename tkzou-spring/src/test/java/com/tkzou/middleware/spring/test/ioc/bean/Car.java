package com.tkzou.middleware.spring.test.ioc.bean;

/**
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 15:33
 */
public class Car {

    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                '}';
    }
}
