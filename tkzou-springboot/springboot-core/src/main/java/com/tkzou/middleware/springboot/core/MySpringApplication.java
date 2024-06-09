package com.tkzou.middleware.springboot.core;

import com.tkzou.middleware.springboot.core.server.WebServer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Map;

/**
 * @author zoutongkun
 */
public class MySpringApplication {

    public static final String MAIN_METHOD = "main";

    public static void run(Class<?> clazz) {
        //推断出真正的主类
        Class<?> realMainClass = deduceMainApplicationClass(MAIN_METHOD);
        // 1.初始化Spring容器
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        //先注册该主类，在refresh方法中扫描这个类以及对应的注解！
        applicationContext.register(realMainClass);
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

    /**
     * 获取指定方法对应的类
     * 这也是springboot在启动获取main方法对应的类的方法
     *
     * @param methodName
     * @return
     */
    private static Class<?> deduceMainApplicationClass(String methodName) {
        try {
            //人为制造一个异常，再通过解析这个异常栈信息来获取！！！
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (methodName.equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

}
