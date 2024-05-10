package com.tkzou.middleware.sms.starter;

import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.provider.factory.SmsProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 自动配置类
 * 在这里统一注入所有bean到ioc容器，使用spi机制实现
 * 不建议单独使用@component或@configuration注解！
 *
 * @author zoutongkun
 */
@Slf4j
@Configuration
//确保其他地方的bean也都扫描到！
@ComponentScan("com.tkzou.middleware.sms")
public class SmsAutoConfiguration {

    /**
     * 注入配置
     * 这里是使用map的方式封装，因为服务提供商不止一个
     * 使用map就更方便啦！
     */
    @Bean
    @ConfigurationProperties(prefix = "sms.blends")
    protected Map<String, Map<String, Object>> blends() {
        return new LinkedHashMap<>();
    }

    /**
     * 注入核心启动类bean
     * 这里就会字段自动注入上面的blends对象
     *
     * @param factoryList
     * @param smsCommonConfig
     * @param blends
     * @return
     */
    @Bean
    protected SmsProviderInitializer smsBlendsInitializer(List<SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig>> factoryList,
                                                          SmsCommonConfig smsCommonConfig,
                                                          Map<String, Map<String, Object>> blends) {
        return new SmsProviderInitializer(factoryList, smsCommonConfig, blends);
    }
}
