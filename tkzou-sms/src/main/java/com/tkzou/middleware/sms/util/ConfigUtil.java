package com.tkzou.middleware.sms.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * ConfigUtil
 * <p> 读取配置文件工具
 *
 * @author :zoutongkun
 * 2024/4/7  21:39
 **/
@Component
public class ConfigUtil {

    private final Environment environment;

    public ConfigUtil(Environment environment) {
        this.environment = environment;
    }

    public String getValue(String key) {
        return environment.getProperty(key);
    }

    public <T> T getValue(String key, Class<T> type) {
        return environment.getProperty(key, type);
    }
}
