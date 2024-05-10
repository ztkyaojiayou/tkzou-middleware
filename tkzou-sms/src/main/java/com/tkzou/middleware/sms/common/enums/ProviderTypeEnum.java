package com.tkzou.middleware.sms.common.enums;

/**
 * 支持的短信服务商
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2024/4/7 10:49
 */
public enum ProviderTypeEnum {
    ALIBABA("alibaba", "阿里云短信"),
    TENCENT("tencent", "腾讯云短信"),
    HUAWEI("huawei", "华为云短信"),
    ;

    private String code;
    private String desc;

    ProviderTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    /**
     * 根据code获取对应的枚举
     *
     * @param code
     * @return
     */
    public static ProviderTypeEnum from(String code) {
        for (ProviderTypeEnum value : ProviderTypeEnum.values()) {
            //
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
