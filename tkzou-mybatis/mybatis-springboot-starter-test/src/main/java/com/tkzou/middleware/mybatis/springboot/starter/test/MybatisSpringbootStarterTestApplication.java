package com.tkzou.middleware.mybatis.springboot.starter.test;

import com.tkzou.middleware.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zoutongkun
 */
@SpringBootApplication
@MapperScan("com.tkzou.middleware.mybatis.springboot.starter.test.mapper")
public class MybatisSpringbootStarterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisSpringbootStarterTestApplication.class, args);
    }

}
