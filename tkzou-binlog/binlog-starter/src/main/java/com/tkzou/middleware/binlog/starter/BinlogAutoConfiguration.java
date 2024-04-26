package com.tkzou.middleware.binlog.starter;

import com.tkzou.middleware.binlog.core.BinlogPositionHandler;
import com.tkzou.middleware.binlog.core.config.RedisConfig;
import com.tkzou.middleware.binlog.starter.config.BinlogAutoProperties;
import com.tkzou.middleware.binlog.starter.init.BinlogHandlerInitBeanProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * binlog自动配置类
 *
 * @author zoutongkun
 */
@AutoConfigureOrder(10)
@EnableConfigurationProperties(BinlogAutoProperties.class)
@ConditionalOnProperty(prefix = "spring.binlog4j", name = "enable", havingValue = "true", matchIfMissing = true)
public class BinlogAutoConfiguration {

    @Autowired(required = false)
    private BinlogPositionHandler positionHandler;

    @Bean
    public BinlogHandlerInitBeanProcessor binlog4jAutoInitializing(BinlogAutoProperties properties) {
        RedisConfig redisConfig = properties.getRedisConfig();
        properties.getClientConfigs().forEach((clientName, clientConfig) -> {
            if (positionHandler != null) {
                clientConfig.setPositionHandler(positionHandler);
            }
            if (redisConfig != null && clientConfig.getRedisConfig() == null) {
                clientConfig.setRedisConfig(redisConfig);
            }
        });
        return new BinlogHandlerInitBeanProcessor(properties.getClientConfigs());
    }
}
