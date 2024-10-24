package com.tkzou.middleware.transaction.mqretry.v3.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单监听者/消费者
 *
 * @author zoutongkun
 */
@Component
public class OrderServiceListener extends BaseMessageRetryService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceListener.class);

    /**
     * 监听订单系统下单成功消息
     *
     * @param message
     */
    @RabbitListener(queues = "mq.order.add")
    public void consume(Message message) {
        log.info("收到订单下单成功消息: {}", message.toString());
        super.initMessage(message);
    }


    @Override
    protected void execute(MessageRetryDTO messageRetryDto) {
        //调用扣减库存服务，将业务异常抛出来
    }

    @Override
    protected void successCallback(MessageRetryDTO messageRetryDto) {
        //todo 业务处理成功，回调
    }

    @Override
    protected void failCallback(MessageRetryDTO messageRetryDto) {
        //todo 业务处理失败，回调
    }
}
