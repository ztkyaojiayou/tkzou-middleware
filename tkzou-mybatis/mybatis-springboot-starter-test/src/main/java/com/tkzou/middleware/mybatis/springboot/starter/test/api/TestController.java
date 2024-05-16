package com.tkzou.middleware.mybatis.springboot.starter.test.api;

import com.tkzou.middleware.mybatis.springboot.starter.test.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> 测试接口 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 22:33
 */
@RestController
@RequestMapping("/mybatis-tkzou")
public class TestController {

    @Autowired
    private DemoMapper demoMapper;

    @GetMapping("/test")
    public Object test() {
        System.out.println("hello,mybatis-tkzou!");
        return this.demoMapper.findOne(1);
    }

}
