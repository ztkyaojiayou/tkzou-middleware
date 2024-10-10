package com.tkzou.middleware.transaction.mqretry.v1.config;

/**
 * @author zoutongkun
 * @description: TODO
 * @date 2023/4/15 15:01
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义KafkaTemplate-注意是使用了自定义的分区器
 * 此时就不能使用springboot自动配置的KafkaTemplate了，因为springboot在kafka的自动配置类上有如下注解：
 *
 * @ConditionalOnMissingBean(KafkaTemplate.class) ，也即此时springboot并没有帮我们自动配置该类了，因为我们自己自定义了！！！
 * 对应的配置类为：KafkaAutoConfiguration
 * 在使用时就需要注入我们自定义的MyPartitionTemplate了！！！
 */
@Configuration
@Slf4j
public class MyKafkaTemplate {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //这不是自动注入，而只是一个普通参数，在 @PostConstruct进行初始化
    KafkaTemplate<String, Object> myKafkaTemplate;

    @Autowired
    KafkaSendCallBackHandler kafkaSendCallBackHandler;

    //PostConstruct的作用：它用来标注一个非静态的方法，用来在spring boot注入一个对象后被调用，对该对象做配置
    @PostConstruct
    public void setMyKafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //注意分区器在这里！！！
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class);
        //新建一个kafkaTemplate
        this.myKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        // 生产者发送完消息后的回调方法，主要用于异常处理
        this.myKafkaTemplate.setProducerListener(kafkaSendCallBackHandler);
    }

    /**
     * 获取自定义的kafkaTemplate
     * 取代原kafkaTemplate
     *
     * @return
     */
    public KafkaTemplate<String, Object> getMyKafkaTemplate() {
        return myKafkaTemplate;
    }

    /**
     * NewTopic 是消费一个没有的topic的消息时框架自动创建的，
     * 一般工作中如果用到了，切记副本数一定要按需求做更改，一般不可能是1，
     * RecordMessageConverter是很早之前我碰到一个没有这个bean的bug，
     * 所以大家如果也遇到了可以这么写一个！
     *
     * @return
     */
    @Bean
    public NewTopic newTopic() {
        //主题 分区数  副本数
        return new NewTopic("test", 1, (short) 1);
    }
}

