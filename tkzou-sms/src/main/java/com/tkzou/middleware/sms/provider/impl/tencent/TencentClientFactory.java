package com.tkzou.middleware.sms.provider.impl.tencent;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.factory.AbstractProviderFactory;
import com.tkzou.middleware.sms.provider.impl.tencent.config.TencentSmsConfig;

/**
 * TencentSmsConfig
 * <p> 建造腾讯云短信
 *
 * @author :zoutongkun
 * 2024/4/8  16:05
 **/
public class TencentClientFactory extends AbstractProviderFactory<TencentSmsClient, TencentSmsConfig> {

    private static final TencentClientFactory INSTANCE = new TencentClientFactory();

    /**
     * 获取建造者实例
     *
     * @return 建造者实例
     */
    public static TencentClientFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 建造一个腾讯云的短信实现
     */
    @Override
    public TencentSmsClient createSmsClient(TencentSmsConfig tencentConfig) {
        return new TencentSmsClient(tencentConfig);
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
