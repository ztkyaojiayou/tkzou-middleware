package com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zoutongkun
 */
@Data
public class QueueConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交换器类型
     */
    private String exchangeType;

    /**
     * 交换器名称
     */
    private String exchangeName;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 路由键key
     */
    private String routingKey;
}
