package com.tkzou.middleware.sms.util;

import cn.hutool.core.io.file.FileReader;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

    /**
     * 读取配置文件
     */
    public static String getConfig(String path) {
        return new FileReader(path).readString();
    }

    public static String getConfig() {
        return getConfig(Objects.requireNonNull(ConfigUtil.class.getResource("/smsConfig.json")).getPath());
    }
}
