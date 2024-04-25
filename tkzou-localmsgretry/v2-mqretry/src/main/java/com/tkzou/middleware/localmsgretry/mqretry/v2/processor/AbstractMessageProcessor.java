package com.tkzou.middleware.localmsgretry.mqretry.v2.processor;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

/**
 * 消息处理器-抽象类
 *
 * @author zoutongkun
 */
@Slf4j
public abstract class AbstractMessageProcessor<E extends Enum<E>> implements IKafkaMessageProcessor<String,
        String>, IBaseMessageProcessor<E> {

    /**
     * msg消息体DTO
     * @param <E>
     */
    @AllArgsConstructor
    protected static class TypeAndMessageJsonObject<E extends Enum<E>> {
        E type;
        Map<Object, Object> messageObject;
    }

    protected abstract Class<E> messageType();

    /**
     * 处理消息
     *
     * @param record
     */
    @Override
    public void processRecord(ConsumerRecord<String, String> record) {
        TypeAndMessageJsonObject<E> msgValueJsonObject;
        msgValueJsonObject = parseMessage(record);
        //再根据type处理消息
        processMessage(record.key(), msgValueJsonObject.type, msgValueJsonObject.messageObject);
    }

    /**
     * 解析消息
     *
     * @param record
     * @return
     */
    protected TypeAndMessageJsonObject<E> parseMessage(ConsumerRecord<String, String> record) {
        final String message = record.value();
        log.info("offset = {} , key = {} , value = {}", record.offset(), record.key(), record.value());
        //这里先把msg转成一个map
        Map<Object, Object> messageObject = JSONUtil.toBean(message, new cn.hutool.core.lang.TypeReference<Map<Object
                , Object>>() {
        }, false);
        String msgType;
        try {
            //解析出消息中的type字段（这是在发送消息时传入的，用于区分不同类型的消息，其实可以直接塞到header里来区分的，更方便！）
            msgType = getMsgType(messageObject);
        } catch (Exception e) {
            throw new RuntimeException("cannot extract type, message:");
        }
        return new TypeAndMessageJsonObject<>(E.valueOf(messageType(), msgType), messageObject);
    }

    /**
     * 获取msg的type
     *
     * @param messageObject
     * @return
     */
    public static String getMsgType(Map<Object, Object> messageObject) {
        return messageObject.get(TYPE_KEY).toString();
    }

}
