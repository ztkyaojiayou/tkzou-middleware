package com.tkzou.middleware.springboot.core.autoconfiguration;

import com.tkzou.middleware.springboot.core.annotation.ConditionalOnClass;
import com.tkzou.middleware.springboot.core.server.JettyWebServer;
import com.tkzou.middleware.springboot.core.server.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 容器bean配置
 *
 * @author zoutongkun
 */
@Configuration
public class WebServerAutoConfiguration implements AutoConfiguration {

    @Bean
    @ConditionalOnClass("org.apache.catalina.startup.Tomcat")
    public TomcatWebServer tomcatWebServer() {
        return new TomcatWebServer();
    }

    @Bean
    @ConditionalOnClass("org.eclipse.jetty.server.Server")
    public JettyWebServer jettyWebServer() {
        return new JettyWebServer();
    }
}
