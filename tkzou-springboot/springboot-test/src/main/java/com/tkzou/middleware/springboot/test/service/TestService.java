package com.tkzou.middleware.springboot.test.service;

import org.springframework.stereotype.Component;

/**
 * @author zoutongkun
 */
@Component
public class TestService {

    public String test(){
        return "tkzou-springboot success!!!";
    }
}
