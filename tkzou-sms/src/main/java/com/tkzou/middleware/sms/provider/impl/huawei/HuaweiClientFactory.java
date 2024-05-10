package com.tkzou.middleware.sms.provider.impl.huawei;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.factory.AbstractProviderFactory;
import com.tkzou.middleware.sms.provider.impl.huawei.config.HuaweiSmsConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * HuaweiSmsConfig
 * <p> 华为短信对象建造
 *
 * @author :zoutongkun
 * 2024/4/8  15:27
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HuaweiClientFactory extends AbstractProviderFactory<HuaweiSmsClient, HuaweiSmsConfig> {

    private static final HuaweiClientFactory INSTANCE = new HuaweiClientFactory();

    /**
     * 获取建造者实例
     *
     * @return 建造者实例
     */
    public static HuaweiClientFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 建造一个华为短信实现
     */
    @Override
    public HuaweiSmsClient createSmsClient(HuaweiSmsConfig huaweiConfig) {
        return new HuaweiSmsClient(huaweiConfig);
    }

    /**
     * 获取供应商
     *
     * @return 供应商
     */
    @Override
    public String getSupplier() {
        return SmsSupplierConstant.HUAWEI;
    }

}
