package com.tkzou.middleware.transaction.mqretry.v1.config;

/**
 * 生产者异步发送消息时的结果监听器
 * Kafka提供了ProducerListener 监听器来异步监听生产者消息是否发送成功，
 * 我们可以自定义一个kafkaTemplate添加ProducerListener，
 * 当消息发送失败我们可以拿到消息进行重试或者把失败消息记录到数据库定时重试。
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/4/15 13:36
 */

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * kafka消息发送后的回调
 * 默认的kafkaTemplate是没有这个回调的，
 * 我们需要将其注入到这个template中才可以生效！
 * 当然，我们在发送消息时也可以直接添加回调，只是整体看起来较为分散，不够优雅！
 *
 * @author zoutongkun
 */
@Component
public class KafkaSendCallBackHandler implements ProducerListener<String, Object> {

    /**
     * 消息发送成功时
     * 何时发送成功？收到ack就认为发送成功，只是kafka返回ack的场景不一样，主要是有三种情况，这里不再赘述
     *
     * @param producerRecord
     * @param recordMetadata
     */
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        System.out.println("消息发送成功：" + producerRecord.toString());
    }

    /**
     * 消息发送失败时，即未收到ack
     * 此时可以配置重试机制或者把失败消息记录到数据库使用job来定时重试
     *
     * @param producerRecord
     * @param recordMetadata
     * @param exception
     */
    @Override
    public void onError(ProducerRecord producerRecord, @Nullable RecordMetadata recordMetadata,
                        Exception exception) {
        System.out.println("消息发送失败：" + producerRecord.toString() + exception.getMessage());
    }
}

