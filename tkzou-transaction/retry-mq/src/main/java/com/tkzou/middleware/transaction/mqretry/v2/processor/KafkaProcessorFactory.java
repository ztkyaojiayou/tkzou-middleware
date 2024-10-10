package com.tkzou.middleware.transaction.mqretry.v2.processor;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.stereotype.Service;

/**
 * kafka消费者工厂
 * 直接从ioc容器拿！
 *
 * @author zoutongkun
 */
@Service
public class KafkaProcessorFactory implements ProcessorFactory {

    @Override
    public IKafkaMessageProcessor create(Class<? extends IKafkaMessageProcessor> clazz) {
        return SpringUtil.getBean(clazz);
    }
}
