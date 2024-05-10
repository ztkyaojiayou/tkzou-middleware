package com.tkzou.middleware.sms.starter;


import com.tkzou.middleware.sms.common.enums.ConfigTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 这是通用配置，如线程池配置等
 *
 * @author zoutongkun
 */
@Data
@Primary
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsCommonConfig {

    /**
     * 配置源类型
     */
    private ConfigTypeEnum configTypeEnum = ConfigTypeEnum.YAML;

    /**
     * 打印banner
     */
    private Boolean isPrint = true;

    /**
     * 是否开启短信限制
     */
    private Boolean restricted = false;

    /**
     * 是否使用redis进行缓存
     */
    private Boolean redisCache = false;

    /**
     * 单账号每日最大发送量
     */
    private Integer accountMax;

    /**
     * 单账号每分钟最大发送
     */
    private Integer minuteMax;

    /**
     * 核心线程池大小
     */
    private Integer corePoolSize = 10;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 30;

    /**
     * 队列容量
     */
    private Integer queueCapacity = 50;

    /**
     * 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
     */
    private Boolean shutdownStrategy = true;

    /**
     * 是否打印http log
     */
    private Boolean HttpLog = false;

}
