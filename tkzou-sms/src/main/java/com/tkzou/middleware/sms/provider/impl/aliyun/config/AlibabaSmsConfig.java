package com.tkzou.middleware.sms.provider.impl.aliyun.config;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.config.BaseSmsProviderConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 阿里云短信配置类，这是一个具体的子类啦！
 * 这些参数是发送阿里云短信所必需的参数
 * 是通过官方文档获取的。
 *
 * @author zoutongkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlibabaSmsConfig extends BaseSmsProviderConfig {

    /**
     * 模板变量名称
     */
    private String templateName;

    /**
     * 请求地址
     */
    private String requestUrl = "dysmsapi.aliyuncs.com";

    /**
     * 接口名称
     */
    private String action = "SendSms";

    /**
     * 接口版本号
     */
    private String version = "2017-05-25";

    /**
     * 地域信息默认为 cn-hangzhou
     */
    private String regionId = "cn-hangzhou";

    @Override
    public String getConfigId() {
        return SmsSupplierConstant.ALIBABA;
    }

    /**
     * 获取供应商
     *
     * @since 3.0.0
     */
    @Override
    public String getSupplier() {
        return SmsSupplierConstant.ALIBABA;
    }

}
