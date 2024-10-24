package com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.service;

import cn.hutool.db.sql.Order;
import com.tkzou.middleware.transaction.mqretry.v3.rabbitdemo.util.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private RabbitUtil rabbitUtil;

    /**
     * 创建订单
     *
     * @param order
     */
    @Transactional
    public void createOrder(Order order) {
        //1、创建订单
        //2、调用库存接口，减库存
        //3、向客户发放红包
        rabbitUtil.convertAndSend("exchange.send.bonus", null, order);
        //4、发短信通知
        rabbitUtil.convertAndSend("exchange.sms.message", null, order);
    }

}
