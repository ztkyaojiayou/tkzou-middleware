package com.tkzou.middleware.localmsgretry.mqretry.v1.producer;

import com.tkzou.middleware.localmsgretry.mqretry.v1.config.MyKafkaTemplate;
import com.tkzou.middleware.localmsgretry.mqretry.v1.dto.BusinessDTO;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * kafka发送消息
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/4/15 13:33
 */
@RestController
@RequestMapping("/provider")
// 这个注解代表这个类开启Springboot事务，若我们在Kafka的配置文件开启了Kafka事务，则需要写上，否则报错！
// @Transactional(rollbackFor = RuntimeException.class)
public class SpringbootProducer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    /**
     * 自定义的kafkaTemplate
     */
    @Autowired
    private MyKafkaTemplate myKafkaTemplate;

    @RequestMapping("/sendMultiple")
    public void sendMultiple() {
        String message = "发送到Kafka的消息";
        for (int i = 0; i < 10; i++) {
            kafkaTemplate.send("test1", "发送到Kafka的消息" + i);
            // 或者使用自定义的kafkaTemplate
            myKafkaTemplate.getMyKafkaTemplate().send("test1", "使用自定义的kafkaTemplate发送的消息！");
            System.out.println(message + i);
        }
    }

    @RequestMapping("/send")
    public void send() {
        // 这个User的代码我没放出来，自己随便写一个实体类，实体类一定要 implements Serializable
        BusinessDTO user = new BusinessDTO(1, "zoutongkun", 29);
        kafkaTemplate.send("test1", user);
        kafkaTemplate.send("test2", "发给test2");
    }

    /**
     * 也可以直接在这里添加回调，但不推荐
     */
    @RequestMapping("/sendWithCallBack")
    public void sendWithCallBack() {
        // 这个User的代码我没放出来，自己随便写一个实体类，实体类一定要 implements Serializable
        BusinessDTO user = new BusinessDTO(1, "zoutongkun", 29);
        ListenableFuture<SendResult<String, Object>> res = kafkaTemplate.send("test1", user);
        //添加回调
        res.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            //发送失败时
            @Override
            public void onFailure(Throwable ex) {
            }

            //发送成功时
            @Override
            public void onSuccess(SendResult<String, Object> result) {
            }
        });

    }

    /**
     * Kafka提供了多种构建消息的方式
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void sendDemo() throws Exception {
        // 1、后面的get代表同步发送，括号内时间可选，代表超过这个时间会抛出超时异常，但是仍会发送成功
        kafkaTemplate.send("test1", "发给test1").get(3, TimeUnit.SECONDS);

        // 2、使用ProducerRecord发送消息
        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("test1", "use ProducerRecord to send message");
        kafkaTemplate.send(producerRecord);

        // 3、使用Message发送消息
        Map<String, Object> map = new HashMap<>();
        map.put(KafkaHeaders.TOPIC, "test1");
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.MESSAGE_KEY, 0);
        GenericMessage<Object> message =
                new GenericMessage<>("use Message to send message", new MessageHeaders(map));
        kafkaTemplate.send(message);
    }
}
