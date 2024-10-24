package com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class DirectConsumeListener {

    /**
     * 监听指定队列，名称：mq.direct.1
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "mq.direct.1")
    public void consumeDirect(Message message, Channel channel) throws IOException {
        log.info("DirectConsumeListener，收到消息: {}", message.toString());
    }

    /**
     * 监听发红包
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "exchange.send.bonus")
    public void consumeBonus(Message message, Channel channel) throws IOException {
        String msgJson = new String(message.getBody(), "UTF-8");
        log.info("收到消息: {}", message.toString());

        //调用发红包接口
    }

    /**
     * 监听发短信
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "exchange.sms.message")
    public void consumeMessage(Message message, Channel channel) throws IOException {
        String msgJson = new String(message.getBody(), "UTF-8");
        log.info("收到消息: {}", message.toString());

        //调用发短信接口
    }
}
