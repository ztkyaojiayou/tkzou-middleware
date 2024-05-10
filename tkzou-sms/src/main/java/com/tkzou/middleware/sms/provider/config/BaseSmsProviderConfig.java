package com.tkzou.middleware.sms.provider.config;

import com.tkzou.middleware.sms.exception.SmsException;
import lombok.Data;

/**
 * 各短信平台的通用配置基类
 * 不同平台的差异化参数则由子类实现，
 * 目前适配主流的三大短信服务平台：阿里云短信、腾讯云短信、华为云短信
 *
 * @author zoutongkun
 * @since 2024/4/20 23:03
 */
@Data
public abstract class BaseSmsProviderConfig implements SmsProviderConfig {

    /**
     * Access Key
     */
    private String accessKeyId;

    /**
     * Access Key Secret
     */
    private String accessKeySecret;

    /**
     * 短信签名
     */
    private String signature;

    /**
     * 模板 ID
     */
    private String templateId;

    /**
     * 权重
     *
     * @since 3.0.0
     */
    private Integer weight = 1;

    /**
     * 配置标识名 如未配置时取对应渠道名例如 Alibaba
     *
     * @since 3.0.0
     */
    private String configId;

    /**
     * 重试间隔（单位：秒），默认为5秒
     */
    private int retryInterval = 5;

    public void setRetryInterval(int retryInterval) {
        if (retryInterval <= 0) {
            throw new SmsException("重试间隔必须大于0秒");
        }
        this.retryInterval = retryInterval;
    }

    /**
     * 重试次数，默认为0次
     */
    private int maxRetries = 0;

    public void setMaxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new SmsException("重试次数不能小于0次");
        }
        this.maxRetries = maxRetries;
    }
}
