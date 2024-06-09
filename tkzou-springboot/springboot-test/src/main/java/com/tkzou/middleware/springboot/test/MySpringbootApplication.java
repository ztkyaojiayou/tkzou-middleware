package com.tkzou.middleware.springboot.test;

import com.tkzou.middleware.springboot.core.MySpringApplication;
import com.tkzou.middleware.springboot.core.annotation.MySpringBootApplication;

/**
 * springboot启动类
 *
 * @author zoutongkun
 */
@MySpringBootApplication
public class MySpringbootApplication {

    public static void main(String[] args) {
        MySpringApplication.run(MySpringbootApplication.class);
    }
}
