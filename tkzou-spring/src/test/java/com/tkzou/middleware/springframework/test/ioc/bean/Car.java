package com.tkzou.middleware.springframework.test.ioc.bean;

import com.tkzou.middleware.springframework.beans.factory.annotation.Value;
import com.tkzou.middleware.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author zoutongkun
 * @Value 注解测试，前提是目标类加了@Component注解
 * @description: TODO
 * @date 2023/8/10 15:33
 */
@Component
public class Car {

    private int price;

    private LocalDate produceDate;

    @Value("${brand}")
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getProduceDate() {
        return produceDate;
    }

    public void setProduceDate(LocalDate produceDate) {
        this.produceDate = produceDate;
    }

    @Override
    public String toString() {
        return "Car{" +
                "price=" + price +
                ", produceDate=" + produceDate +
                ", brand='" + brand + '\'' +
                '}';
    }
}
