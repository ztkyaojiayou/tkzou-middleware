package com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zoutongkun
 */
@Data
public class ProduceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交换器名称
     */
    private String exchangeName;

    /**
     * 路由键key
     */
    private String routingKey;

    /**
     * 消息内容
     */
    public String msg;
}
