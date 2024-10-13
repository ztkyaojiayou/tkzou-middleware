package com.tkzou.middleware.springcloud.registercenter.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 注册中心，本质上就是一个常规的springboot web服务
 * 就是针对服务信息的crud，同时对外提供接口以获取服务信息！！！
 * 就只是基于内存
 * 因为注册中心非重点
 *
 * @author zoutongkun
 * @date 2024/4/28
 */

@SpringBootApplication
public class RegisterCenterServerApplication {
    private static Logger logger = LoggerFactory.getLogger(RegisterCenterServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RegisterCenterServerApplication.class, args);
    }
}
