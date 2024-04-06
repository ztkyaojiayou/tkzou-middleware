package com.tkzou.middleware.springboot.core;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Map;

/**
 * @author zoutongkun
 */
public class MySpringApplication {

    public static void run(Class clazz) {

        // 1.初始化Spring容器
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(clazz);
        applicationContext.refresh();
        //2.启动tomcat
        //2.1找到一个tomcat
        WebServer webServer = getWebServer(applicationContext);
        //2.2启动tomcat
        webServer.start(applicationContext);
    }

    /**
     * 在当前环境中获取一个容器，如tomcat
     *
     * @param applicationContext
     * @return
     */
    public static WebServer getWebServer(WebApplicationContext applicationContext) {
        Map<String, WebServer> beansOfType = applicationContext.getBeansOfType(WebServer.class);
        checkWebServer(beansOfType);
        return beansOfType.values().stream().findFirst().get();
    }

    private static void checkWebServer(Map<String, WebServer> beansOfType) {
        if (beansOfType.isEmpty()) {
            throw new NullPointerException();
        }

        if (beansOfType.size() > 1) {
            throw new IllegalStateException();
        }
    }

}
