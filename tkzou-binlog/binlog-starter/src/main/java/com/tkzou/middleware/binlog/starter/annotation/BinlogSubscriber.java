package com.tkzou.middleware.binlog.starter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * binlog订阅者
 * 因为该注解上已经加了@Component了，
 * 因此只要加了该注解的类就已经是一个bean啦！！！
 * 无需再单独加@component啦！！！
 * 推荐这种写法！
 *
 * @author zoutongkun
 */
@Documented
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BinlogSubscriber {

    /**
     * 客户端
     * <p>
     * 在 spring boot 下, 需指定将该 handler 实例, 注册到哪个 client 客户端
     */
    String clientName();

}
