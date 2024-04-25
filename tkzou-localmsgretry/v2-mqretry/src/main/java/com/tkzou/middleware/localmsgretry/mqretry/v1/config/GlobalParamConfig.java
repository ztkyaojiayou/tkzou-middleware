package com.tkzou.middleware.localmsgretry.mqretry.v1.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author :zoutongkun
 * @date :2023/10/3 1:37 下午
 * @description :
 * @modyified By:
 */
@Configuration
@Data
public class GlobalParamConfig {

    /**
     * 重试次数
     */
    @Value("spring.kafka.consumer.retry-time")
    private Integer retryTime;

}
