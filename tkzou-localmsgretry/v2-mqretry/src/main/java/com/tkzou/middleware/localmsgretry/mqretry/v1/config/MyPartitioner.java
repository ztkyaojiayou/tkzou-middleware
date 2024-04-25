package com.tkzou.middleware.localmsgretry.mqretry.v1.config;

/**
 * 自定义分区策略
 * 然后就可以自定义一个配置类将去注入到KafkaTemplate中
 * 该类无需交由spring管理，因为我们只是使用一下它的class对象！！！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/4/15 15:00
 */

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class MyPartitioner implements Partitioner {
    @Override
    public int partition(String s, Object key, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        String keyStr = key + "";
        if (keyStr.startsWith("0")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}

