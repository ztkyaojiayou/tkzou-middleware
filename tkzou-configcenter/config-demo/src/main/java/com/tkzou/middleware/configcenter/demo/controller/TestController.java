package com.tkzou.middleware.configcenter.demo.controller;

import com.tkzou.middleware.configcenter.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试
 * 
 *
 * @author zoutongkun
 * @date 2022/9/30 00:56
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private UserService userService;

    @GetMapping
    public String get() {
        return userService.getUsername();
    }

}
