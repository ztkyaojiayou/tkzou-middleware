package com.tkzou.middleware.binlog.core;


import com.tkzou.middleware.binlog.core.client.BinlogClient;
import com.tkzou.middleware.binlog.core.common.enums.BinlogClientMode;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.core.config.RedisConfig;
import com.tkzou.middleware.binlog.core.handler.SuperBinlogEventHandler;

/**
 * 测试类
 * 相当于是一个启动类
 */
public class BootStrap {

    public static void main(String[] args) {

        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setHost("127.0.0.1");
        redisConfig.setPort(6379);
        redisConfig.setPassword("taoren@123");

        // Binlog 客户端【配置】 非 Spring
        BinlogClientConfig clientConfig = new BinlogClientConfig();
        clientConfig.setHost("127.0.0.1");
        clientConfig.setPort(3306);
        clientConfig.setUsername("root");
        clientConfig.setPassword("taoren@123");
        clientConfig.setServerId(1990);
        clientConfig.setRedisConfig(redisConfig); // 依赖redis中间件（支撑 持久化模式 与 高可用 集群）
        clientConfig.setPersistence(false); // 持久化模式
        // clientConfig.setMode(BinlogClientMode.cluster); // 集群模式
        clientConfig.setMode(BinlogClientMode.standalone); // 单机模式

        //创建一个client
        BinlogClient binlogClient = BinlogClient.create(clientConfig);
        //注册一个具体的事件处理器
        binlogClient.registerEventHandler(new SuperBinlogEventHandler());
        //连接
        binlogClient.connect();
    }
}
