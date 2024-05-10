package com.tkzou.middleware.sms.provider.impl.huawei.config;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.config.BaseSmsProviderConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zoutongkun
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HuaweiSmsConfig extends BaseSmsProviderConfig {
    /**
     * 国内短信签名通道号
     */
    private String sender;
    /**
     * 短信状态报告接收地
     */
    private String statusCallBack;
    /**
     * APP接入地址
     */
    private String url;

    @Override
    public String getConfigId() {
        return SmsSupplierConstant.HUAWEI;
    }

    /**
     * 获取供应商
     *
     * @since 3.0.0
     */
    @Override
    public String getSupplier() {
        return SmsSupplierConstant.HUAWEI;
    }

}
