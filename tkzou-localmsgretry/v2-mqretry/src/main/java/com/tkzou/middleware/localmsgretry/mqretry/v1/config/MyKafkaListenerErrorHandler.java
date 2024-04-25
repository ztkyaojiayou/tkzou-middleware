package com.tkzou.middleware.localmsgretry.mqretry.v1.config;

/**
 * 消费者端的异常回调处理
 * @author zoutongkun
 * @description: TODO
 * @date 2023/4/15 13:41
 */

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;

@Configuration
public class MyKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception) {
        return new Object();
    }

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        System.out.println("消息详情：" + message);
        System.out.println("异常信息：：" + exception);
        System.out.println("消费者详情：：" + consumer.groupMetadata());
        System.out.println("监听的topic：：" + consumer.listTopics());
        return KafkaListenerErrorHandler.super.handleError(message, exception, consumer);
    }
}
