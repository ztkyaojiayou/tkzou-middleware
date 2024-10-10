package com.tkzou.middleware.transaction.mqretry.v1.producer.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 时间戳拦截器
 *
 * @author :zoutongkun
 * @date :2022/8/4 7:51 下午
 * @description :
 * @modyified By:
 */
public class TimeInterceptor implements ProducerInterceptor<String, String> {
    /**
     * 1）方法1：在发送消息之前执行 该方法封装进 KafkaProducer.send 方法中， 即它运行在用户主线程中。 Producer 确保在消息被序列化以及计算分区前调用该方法。
     * 用户可以在该方法中对消息做任何操作， 但最好 保证不要修改消息所属的 topic 和分区， 否则会影响目标分区的计算。
     *
     * <p>需求：给每一个消息值加一个时间戳
     *
     * @param record
     * @return
     */
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 不能直接set值，就只能使用构造函数新建了
        return new ProducerRecord<>(
            record.topic(),
            record.partition(),
            record.key(),
            System.currentTimeMillis() + "," + record.value());
    }

    /**
     * 2）方法2：该方法会在消息从 RecordAccumulator 成功发送到 Kafka Broker 之后， 或者在发送过程 中失败时调用。 并且通常都是在 producer
     * 回调逻辑触发之前。 onAcknowledgement 运行在 producer 的 IO 线程中， 因此不要在该方法中放入很重的逻辑， 否则会拖慢 producer 的消息 发送效率。
     *
     * @param metadata
     * @param exception
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    /**
     * 3）方法3：关闭 interceptor，主要用于执行一些资源清理工作
     */
    @Override
    public void close() {
    }

    /**
     * 4）方法4：获取配置信息和初始化数据时调用。
     */
    @Override
    public void configure(Map<String, ?> configs) {
    }
}
