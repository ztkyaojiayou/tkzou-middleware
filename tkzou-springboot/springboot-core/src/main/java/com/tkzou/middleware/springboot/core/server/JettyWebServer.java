package com.tkzou.middleware.springboot.core.server;

import org.springframework.web.context.WebApplicationContext;

/**
 * @author zoutongkun
 */
public class JettyWebServer implements WebServer {

    @Override
    public void start(WebApplicationContext webApplicationContext) {
        System.out.println("启动Jetty");
    }
}
