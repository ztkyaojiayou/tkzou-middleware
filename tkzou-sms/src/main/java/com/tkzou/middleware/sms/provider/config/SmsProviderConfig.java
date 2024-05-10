package com.tkzou.middleware.sms.provider.config;

/**
 * SmsSupplierConfig
 * <p> 各短信平台的配置类接口
 *
 * @author :zoutongkun
 * 2024/5/16  15:14
 **/
public interface SmsProviderConfig {

    /**
     * 获取配置标识名
     *
     * @return
     */
    String getConfigId();

    /**
     * 获取短信平台供应商
     *
     * @return
     */
    String getSupplier();

}
