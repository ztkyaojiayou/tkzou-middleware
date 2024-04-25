package com.tkzou.middleware.localmsgretry.mqretry.v1.consumer;

import com.tkzou.middleware.localmsgretry.mqretry.v1.constant.RetryConstant;
import com.tkzou.middleware.localmsgretry.mqretry.v1.entity.FailMsgLog;
import com.tkzou.middleware.localmsgretry.mqretry.v1.mapper.FailMsgLogMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 消息记录-消费者
 * 监听消息发送成功和失败的topic（消息消费成功或失败都会通过一个topic来通知）
 * 这里并不直接重试！主要是维护本地消息表：
 * 1.成功消费时，若为重试消息，则更新重试状态为“成功”
 * 2.消费失败时，若为重试消息，则入库，若为非重复消息，则入库，重试状态置为“失败”
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/10/03 00:41
 */
@Component
@Slf4j
public class FailMsgConsumer {

    @Autowired
    private FailMsgLogMapper failMsgLogMapper;

    /**
     * 监听记录消息消费成功的topic
     * 该topic里只有需要重试的消息的id，我们只需将这些消息在db中置成重试完成即可！
     *
     * @param record
     * @param ack
     * @param topic  即通过@Header来获取该消息所属的topic（而不是当前消费者订阅的topic！），该注解是常用的，务必掌握！！！
     */
    @KafkaListener(topics = RetryConstant.MSG_SUCCESS_RECORD_TOPIC, groupId = "test")
    public void onSuccess(ConsumerRecord<?, ?> record, Acknowledgment ack,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("msg_success_record_topic 消费了： Topic:" + topic + ",Message:" + msg);
            JSONObject json = JSONObject.fromObject(record.value());
            //重试消息的标识，也即这条重试消息数据库中的id
            int id = json.getInt("id");
            FailMsgLog failMsgLog = new FailMsgLog();
            failMsgLog.setId(id);
            //更新一下这条消息的状态，即表示该重试消息被消费成功了！
            //todo 此时可以使用一个job定时清理，或者其实可以不入库，在这里就直接删除了！
            failMsgLog.setMqStatus(2);
            int affect = failMsgLogMapper.updateById(failMsgLog);
            log.info("updateSuccessMq affect:{},{}", id, affect);
            //手动提交offset
            ack.acknowledge();
        }
    }

    /**
     * 监听记录消息消费失败的topic
     * 两种消息都有，即第一次消费就失败的消息和带重试标识的消息
     *
     * @param record
     * @param ack
     * @param topic
     */
    @KafkaListener(topics = RetryConstant.MSG_FAIL_RECORD_TOPIC, groupId = "test")
    public void onFail(ConsumerRecord<?, ?> record, Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("msg_fail_record_topic 消费了： Topic:" + topic + ",Message:" + msg);
            JSONObject json = JSONObject.fromObject(record.value());
            String curTopic = json.getString("topic");
            String curKey = json.getString("key");
            String curValueStr = json.getString("value");
            JSONObject curValueObj = JSONObject.fromObject(curValueStr);
            //1.若带有重试标识，则为重试消息，则db中已有该记录，则无需入库，
            // 也无需要更新状态（因为这里并不重试，只是入库，重试由job专门处理！），
            // 因为还是没消费成功，还需要再次重试，直到消费成功！（目前该方案中是重试6次，其实可以做成配置！）
            if (curValueObj.has(RetryConstant.RESEND_FLAG)) {
                return;
            }
            //2.否则，即为新的失败消息，入库
            FailMsgLog failMsgLog = new FailMsgLog();
            failMsgLog.setTopic(curTopic);
            failMsgLog.setMqKey(curKey);
            failMsgLog.setMqValue(curValueStr);
            //重试次数置为0
            failMsgLog.setResendTimes(0);
            //置为未完成处理
            failMsgLog.setMqStatus(1);
            failMsgLogMapper.insert(failMsgLog);
            //手动提交offset
            ack.acknowledge();
        }
    }
}
