package com.tkzou.middleware.springboot.core.server;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * tomcat容器
 *
 * @author zoutongkun
 */
public class TomcatWebServer implements WebServer {

    @Override
    public void start(WebApplicationContext applicationContext) {
        startTomcat(applicationContext);
        System.out.println("启动了Tomcat");

    }

    /**
     * 启动tomcat，带起/注册springMVC中的servlet，
     * 也即部署项目中的controller到tomcat
     *
     * @param applicationContext
     */
    public static void startTomcat(WebApplicationContext applicationContext) {

        Tomcat tomcat = new Tomcat();

        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");

        Connector connector = new Connector();
        //设置监听端口
        connector.setPort(8081);

        Engine engine = new StandardEngine();
        engine.setDefaultHost("localhost");

        Host host = new StandardHost();
        host.setName("localhost");

        String contextPath = "";
        Context context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());

        host.addChild(context);
        engine.addChild(host);

        service.setContainer(engine);
        service.addConnector(connector);
        //添加springMVC中的servlet，相当于部署自己的服务到tomcat
        tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet(applicationContext));
        context.addServletMappingDecoded("/*", "dispatcher");

        try {
            //启动tomcat
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

    }
}
