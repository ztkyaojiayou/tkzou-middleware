package com.tkzou.middleware.binlog.starter.config;

import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.core.config.RedisConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * binlog配置类
 *
 * @author zoutongkun
 */
@Configuration
@ConfigurationProperties(prefix = "spring.binlog4j")
public class BinlogAutoProperties {

    private boolean enable = true;

    private Map<String, BinlogClientConfig> clientConfigs;

    @NestedConfigurationProperty
    private RedisConfig redisConfig;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, BinlogClientConfig> getClientConfigs() {
        return clientConfigs;
    }

    public void setClientConfigs(Map<String, BinlogClientConfig> clientConfigs) {
        this.clientConfigs = clientConfigs;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

}
