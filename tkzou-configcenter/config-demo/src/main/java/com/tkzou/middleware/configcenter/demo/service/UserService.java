package com.tkzou.middleware.configcenter.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 
 *
 * @author zoutongkun
 * @date 2022/10/12 12:56
 */
@RefreshScope
@Service
public class UserService {

    @Value("${tkzou.username}")
    private String username;

    public String getUsername() {
        return username;
    }

}
