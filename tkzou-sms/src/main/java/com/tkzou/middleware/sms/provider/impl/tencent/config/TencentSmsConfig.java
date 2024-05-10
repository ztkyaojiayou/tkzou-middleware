package com.tkzou.middleware.sms.provider.impl.tencent.config;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.config.BaseSmsProviderConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zoutongkun
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TencentSmsConfig extends BaseSmsProviderConfig {

    /**
     * 地域信息默认为 ap-guangzhou
     */
    private String territory = "ap-guangzhou";

    /**
     * 请求超时时间
     */
    private Integer connTimeout = 60;
    /**
     * 请求地址
     */
    private String requestUrl = "sms.tencentcloudapi.com";
    /**
     * 接口名称
     */
    private String action = "SendSms";

    /**
     * 接口版本
     */
    private String version = "2021-01-11";

    /**
     * 服务名
     */
    private String service = "sms";

    @Override
    public String getConfigId() {
        return SmsSupplierConstant.TENCENT;
    }

    /**
     * 获取供应商
     *
     * @since 3.0.0
     */
    @Override
    public String getSupplier() {
        return SmsSupplierConstant.TENCENT;
    }

}
