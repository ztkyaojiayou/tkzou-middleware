package com.tkzou.middleware.sms.util;

import cn.hutool.core.io.file.FileReader;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * SettingUtil
 * <p> 用于读取json配置文件
 *
 * @author :zoutongkun
 * 2024/4/8  14:29
 **/
@Component
public class SettingUtil {

    /**
     * 读取配置文件
     */
    public static String getSetting(String path) {
        return new FileReader(path).readString();
    }

    public static String getSetting() {
        return getSetting(Objects.requireNonNull(SettingUtil.class.getResource("/smsConfig.json")).getPath());
    }
}
