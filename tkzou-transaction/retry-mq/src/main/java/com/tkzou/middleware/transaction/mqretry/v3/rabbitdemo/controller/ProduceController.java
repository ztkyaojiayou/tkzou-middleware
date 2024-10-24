package com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.controller;

import com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.dto.ProduceInfo;
import com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.util.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produce")
public class ProduceController {

    @Autowired
    private RabbitUtil rabbitUtil;

    /**
     * 发送消息到交换器
     *
     * @param produceInfo
     */
    @PostMapping("sendMessage")
    public void sendMessage(@RequestBody ProduceInfo produceInfo) {
        rabbitUtil.convertAndSend(produceInfo.getExchangeName(), produceInfo.getRoutingKey(), produceInfo);
    }

}
