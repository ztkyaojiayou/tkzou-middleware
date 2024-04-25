//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tkzou.middleware.localmsgretry.mqretry.v2.processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * kafka消息处理器
 *
 * @author zoutongkun
 */
public interface IKafkaMessageProcessor<K, V> {
    /**
     * 处理消息
     *
     * @param record
     */
    void processRecord(ConsumerRecord<K, V> record);
}
