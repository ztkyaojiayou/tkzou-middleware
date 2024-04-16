package com.tkzou.middleware.localmsgretry.mqretry.util;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Properties;

/**
 * kafka工具类
 * 也可以单独定义一个接口，再注入KafkaTemplate，再封装里面的一些常用方法，
 * 其实没有必要，直接搞一个工具类获取到KafkaTemplate，再直接使用它的方法就完事儿了！！！
 * kafka是这样，redis也可以是这样
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/9/27 18:30
 */
public class KafkaUtil {

    /**
     * 注入kafkaTemplate
     * 单例模式
     */
    private static final KafkaTemplate kafkaTemplate =
            SpringUtil.getApplicationContext().getBean(KafkaTemplate.class);

    /**
     * 对外统一提供，此时就无需在每一处都使用@Autowired注入一次了！！！
     *
     * @return
     */
    public static KafkaTemplate getKafkaTemplateInstance() {
        return kafkaTemplate;
    }

    /**
     * 生成一个consumer对象
     *
     * @param groupId      消费者组id
     * @param topics       主题
     * @param isAutoCommit 是否自动提交
     * @return
     */
    public static KafkaConsumer<String, String> getConsumerInstance(String kafkaServerIp, String groupId,
                                                                    List<String> topics,
                                                                    Boolean isAutoCommit) {

        //1.获取基础配置信息
        Properties props = geConsumerBaseProperties(kafkaServerIp, groupId);
        //2.设置是否为自动提交
        if (isAutoCommit) {
            // 1.开启自动提交--即字自动提交offset（默认，且默认时间间隔为2s）
            // 即在消费消息之前，先拿到offset值（但只拿一次，之后就不会再去zookeeper或kafka本地拿了，
            // 而是会在内存中获取，即该值也会在内存中存储并维护一份），开始从该值对应的消息消费起，
            // 且是每隔相同时间就自动更新该值(也即到点就提交，而不是消费成功后才更新！），即写入zookeeper或kafka本地，同时也会更新内存中的该值，
            // 下次消费就会从该值对应的消息消费，可以通过auto.commit.interval.ms：来配置自动提交offset的时间间隔，默认是5s，
            // 这里会因为业务和自动提交的时间的原因而出现两种异常情况：
            // 1）若消息消费成功，但提交该消息对应的offset失败，则下次消费时还会从原来的offset对应的消息消费，
            // 也易知，会发生消息的重复消费
            //（要明确的是，kafka的消息由于是通过发布/订阅的方式实现的，
            // 因此它的消息是会被持久化的，也即即便消息被消费了，但依然不会被删除，默认是保存7天，但可以自定义设置！！！）。
            //2）若消息还未消费成功就已经到提交时间了，就提交了对应的offset值，则易知会漏消息！！！
            props.put("enable.auto.commit", "true");
            //这里设置自动提交的时间间隔为1s
            props.put("auto.commit.interval.ms", "1000");
        } else {
            // 2.关闭自动提交 offset，则需要手动提交！
            // 当关闭自动提交时，则需要在处理完这些消息时手动提交，
            // 且在实际生产过程中，一般还会以事务的方式同时存入一份到mysql，
            // 即自己维护这个offset，这样就可以解决很多问题了！！！
            // 比如，消费端就完全不会丢失数据啦，即所有的消息都可以消费完毕。
            // 分为两种：同步提交和异步提交
            // 1.同步提交：当前线程会阻塞，直到offset提交成功，才会去拉取第二批消息
            // 2.异步提交：即会启动一个新的线程提交，不影响拉取下一个批次的消息，很明显效率更高
            // 它也有两种方式，带回调和不带回调的方式，一般会使用带回调的方式

            //  此是可能出现的问题：
            //  也会出现消息重复消费（即当异步提交时突然挂掉时）和消息未被成功处理但被跳过了
            // （也是当消息执行失败，但是offset却已经更新了时）的问题
            // 因此易知，重复消费无可避免，消息未被成功处理但被跳过了也无可避免
            // 对于重复消费的问题，我们可以使用幂等性来确保不影响业务；
            // 对于消息未被成功处理但被跳过了的问题，
            //    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

            // 3.关于重复消费和漏消费的总结：
            // 无论是同步提交还是异步提交 offset，都有可能会造成数据的漏消费或者重复消费。
            // 先提交offset后消费，有可能造成数据的漏消费（因为可能消费失败），
            // 而先消费后提交offset，有可能会造成数据的重复消费（因为可能提交失败），
            // 针对这个问题，kafka提供了自定义存储offset的方案，也即允许我们自己维护offset，比如存储在数据库中，然后手动维护！
            // 也即消费者使用事务（但并非kafka提供的事务机制，是广义上的方案），
            // 易知如果想完成Consumer端的精准一次性消费，那么需要Kafka消费端将消费过程和提交offset过程做原子绑定，
            // 此时我们需要将Kafka的offset保存到支持事务的自定义介质（比如MySQL）中，demo在CustomSaveOffset中。
            props.put("enable.auto.commit", "false");
        }
        //3.创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        //4.订阅topic，可多个
        consumer.subscribe(topics);
        return consumer;
    }

    /**
     * 获取消费者基础配置
     *
     * @param kafkaServerIp
     * @param groupId
     * @return
     */
    private static Properties geConsumerBaseProperties(String kafkaServerIp, String groupId) {
        Properties props = new Properties();
        //1.连接kafka
        props.put("bootstrap.servers", kafkaServerIp);
        // 2.配置消费者组，只要 group.id 相同，就属于同一个消费者组
        // 那么什么是一个消费者呢？
        // 一个带有@KafkaListener注解的方法就是一个消费者，同时还可以通过参数concurrency来控制同一个消费者的并发数，
        // 比如，假设我们有一个消费者，同时还配置了该值为5，则表示实际上是有5个消费者线程在进行消费，也即相当于有5个消费者了，
        // 同理，既然他们也是消费者，那么一个消费者也只能消费一个分区的消息，
        // concurrency值对应@KafkaListener的消费者实例线程数目，
        // 如果concurrency数量大于partition数量，多出的部分分配不到partition，会被闲置。
        // 同时，设置的并发量不能大于partition的数量，如果需要提高吞吐量，可以通过增加partition的数量达到快速提升吞吐量的效果。

        // 且一个项目中可以有多个带该注解的方法，也即可以有多个消费者！！！
        // 同时该注解可以配置消费者组id，消费者id就决定了这个消费者归属于哪个消费者组，
        // 那么也即，同一个项目中，所有的消费者可以就属于同一个消费者组，
        // 也有可能多个消费者归属于多个消费者组，但可以都订阅同一个topic！
        // 而一般而言，一个springboot服务会全局配置/定义一个consumer-group-id，
        // 此时若项目中的所有带@KafkaListener注解的方法没有额外配置groupId，则项目中的所有消费者都属于同一个消费者组，
        // 而若有单独配置，则以单独的配置项为准。
        // 当该服务在k8s上部署了多个实例/pod时，也是一样的，
        // 若单纯横向扩展而不修改配置，那么本质上还是一样的，只是相同消费者组下的消费者多了而已，
        // 它们会一起消费同一个topic下不同分区的消息!
        // 参考：https://blog.csdn.net/justlpf/article/details/129091732
        props.put("group.id", groupId);

        // 3.对拉取的信息进行反序列化（因为生产者生产的消息会先序列化后再发送到kafka）
        // kafka提供了StringDeserializer这个类来实现反序列化
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        // 4.重置消费者的offset
        // 表示：当当前消费者组订阅了一个新topic后，订阅之前的消息也会被消费，默认是不开启的
        // 默认使用的是latest，即从最新的一条消息消费，也即只能消费订阅之后的产生的消息
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");//--from-beginning
        // 注意：当前消费者订阅哪些主题并不在这里配置，而是在实际要消费时指定！！！
        return props;
    }

    /**
     * 获取一个基础的生产者
     * 也可以直接从ioc容器中获取一个默认的生产者，即：
     *
     * @return
     * @Autowired KafkaProducer<String, String> kafkaProducer;
     */
    public static KafkaProducer<String, String> getKafkaProducerInstance() {
        String kafkaServerIp = "127.0.0.1:9092";
        Properties props = getProducerBaseProperties(kafkaServerIp);
        return new KafkaProducer<>(props);
    }

    /**
     * 获取一个springboot默认提供的生产者
     *
     * @return
     */
    public static KafkaProducer<String, String> getKafkaDefaultProducerInstance() {
        return SpringUtil.getApplicationContext().getBean(KafkaProducer.class);
    }

    /**
     * 获取一个带拦截器的生产者
     *
     * @return
     */
    public static KafkaProducer<String, String> getKafkaProducerInstance(List<String> interceptors) {
        String kafkaServerIp = "127.0.0.1:9092";
        Properties props = getProducerBaseProperties(kafkaServerIp);

        // 定义为自定义的分区逻辑
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "MyPartitioner.class");

        // 添加拦截器--生产端无需做任何改变
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
        return new KafkaProducer<>(props);
    }

    /**
     * 获取一个带拦截器和自定义分区器的生产者
     *
     * @param interceptors
     * @param partitionerClazzName 分区器的类名（纯类名即可，而无需全限定类名）
     * @return
     */
    public static KafkaProducer<String, String> getKafkaProducerInstance(List<String> interceptors,
                                                                         String partitionerClazzName) {
        String kafkaServerIp = "127.0.0.1:9092";
        Properties props = getProducerBaseProperties(kafkaServerIp);

        // 定义为自定义的分区逻辑
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerClazzName + ".class");

        // 添加拦截器--生产端无需做任何改变
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
        return new KafkaProducer<>(props);
    }

    /**
     * 获取生产者基础配置
     *
     * @param kafkaServerIp kafka服务器ip
     * @return
     */
    private static Properties getProducerBaseProperties(String kafkaServerIp) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServerIp);
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
