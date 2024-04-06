package com.tkzou.middleware.springboot.core;

import org.springframework.web.context.WebApplicationContext;

/**
 * @author zoutongkun
 */
public interface WebServer {
    /**
     * 启动容器
     *
     * @param applicationContext
     */
    void start(WebApplicationContext applicationContext);
}
