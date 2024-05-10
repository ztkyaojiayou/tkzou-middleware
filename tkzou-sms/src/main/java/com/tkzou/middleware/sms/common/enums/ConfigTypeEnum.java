package com.tkzou.middleware.sms.common.enums;

/**
 * ConfigType
 * <p>配置文件类型
 *
 * @author :zoutongkun
 * 2024/4/5  19:08
 **/
public enum ConfigTypeEnum {
    /**
     * yaml配置文件
     */
    YAML("yaml"),
    /**
     * 接口
     */
    INTERFACE("interface");

    private final String name;

    ConfigTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
