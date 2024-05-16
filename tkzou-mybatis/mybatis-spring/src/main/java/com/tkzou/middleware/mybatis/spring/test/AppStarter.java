package com.tkzou.middleware.mybatis.spring.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.date.DateTime;
import com.tkzou.middleware.mybatis.spring.config.MyBatisConfig;
import com.tkzou.middleware.mybatis.spring.test.entity.User;
import com.tkzou.middleware.mybatis.spring.test.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 单独使用spring时的启动类
 * 而在springboot之前，当前启动类中的逻辑是通过tomcat启动的！
 *
 * @author :zoutongkun
 * @date :2024/5/14 11:46 下午
 * @description :
 * @modyified By:
 */
public class AppStarter {
    public static void main(String[] args) {
        // Logback运行时动态更改日志级别
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ROOT").setLevel(Level.INFO);

        // spring应用上下文 -- 加载配置类，执行自动扫描
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(MyBatisConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
//        System.out.println(JSONUtil.toJsonStr(userService.findOne(1)));
        userService.save(User.builder().name(DateTime.now() + "tkzou").age(18).build());
    }
}
