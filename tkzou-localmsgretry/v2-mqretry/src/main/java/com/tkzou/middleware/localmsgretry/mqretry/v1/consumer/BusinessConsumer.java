package com.tkzou.middleware.localmsgretry.mqretry.v1.consumer;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.localmsgretry.mqretry.v1.constant.RetryConstant;
import com.tkzou.middleware.localmsgretry.mqretry.v1.dto.BusinessDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 业务--消费者
 * 可能会消费重试消息
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/10/03 00:41
 */
@Component
@Slf4j
public class BusinessConsumer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 注入方式2
     */
    private static KafkaTemplate kafkaTemplate02 =
            SpringUtil.getApplicationContext().getBean(KafkaTemplate.class);

    @KafkaListener(topics = "test", groupId = "test")
    public void consumerMsg(ConsumerRecord<?, ?> record, Acknowledgment ack,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            JSONObject json = JSONObject.fromObject(record.value());
            log.info("test 消费了： Topic:" + topic + ",Message:" + json);
            try {
                BusinessDTO businessDTO = JSONUtil.toBean(json.toString(), BusinessDTO.class);
                //消费消息，业务处理
                Boolean result = processMessage(businessDTO);
                if (result) {
                    //成功时
                    handleSuccessMsg(record);
                } else {
                    //失败时
                    handleFailMsg(record);
                }
                //记得catch起来！
            } catch (Exception e) {
                //失败时
                handleFailMsg(record);
                log.error("", e);
            }
            //最后再手动提交offset，这和myConsumer.commitSync();是一样的，只是写法不同！！！
            ack.acknowledge();
        }

    }

    /**
     * 消费消息，业务处理
     *
     * @param value
     */
    private Boolean processMessage(Object value) {
        //处理业务
        return false;
    }

    /**
     * 消息成功消费时，判断是否是需要重试的消息，若是，则修改库中该消息的处理状态为处理完成（也即消费成功！）
     * 要注意的是，这个消息有两种情况：第一次消费就失败的消息和带重试标识的消息，
     * 但只处理后者
     *
     * @param record
     */
    private void handleSuccessMsg(ConsumerRecord<?, ?> record) {
        JSONObject valueObj = JSONObject.fromObject(record.value());
        //判断该消息是否有重试标识（因为若是需要重试的消息，在处理时会加上该标识），
        //也即只处理需要重试的消息，非重试的消息跳过，也即该topic中的消息全是要重试的
        if (valueObj.has(RetryConstant.RESEND_FLAG)) {
            //使用id做的重试标识
            int id = valueObj.getInt(RetryConstant.RESEND_FLAG);
            //只发送一个带有重试标识的消息到“发送成功”的topic，也即只要能标识即可，相当于向该topic传递一下该id，
            //之后再去消费端更改一下数据库中该消息的状态即可
            JSONObject newJsonObj = new JSONObject();
            newJsonObj.put("id", id);
            kafkaTemplate.send(RetryConstant.MSG_SUCCESS_RECORD_TOPIC, newJsonObj.toString());
        }
    }

    /**
     * 消息消费失败时，发送到“方式失败”topic，下次重试
     * 要注意的是，这个消息也一样有两种情况：第一次消费就失败的消息和带重试标识的消息
     * 且都要入库，即重试的消息要是还失败，则继续入库，只是在库中更改一下该消息被重试的次数
     * 易知，可以做到对一条消息不断重试，直到成功为止，符合我们的预期！！！
     * 疑问：为什么还要通过发消息啊？直接入库不就得了？
     * 主要也是削峰，异步处理，提高并发性能！
     *
     * @param record
     */
    private void handleFailMsg(ConsumerRecord<?, ?> record) {
        String key = record.key() != null ? String.valueOf(record.key()) : "";
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("topic", record.topic());
        jsonObj.put("key", key);
        //该消息中可能带有重试标识，也即可能是需要重试的消息，后面会去重，只入库新消息
        jsonObj.put("value", record.value());
        kafkaTemplate.send(RetryConstant.MSG_FAIL_RECORD_TOPIC, jsonObj.toString());
    }

}
