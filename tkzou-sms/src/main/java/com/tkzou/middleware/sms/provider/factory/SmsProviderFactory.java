package com.tkzou.middleware.sms.provider.factory;

import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;

/**
 * AlibabaSmsConfig
 * <p>短信对象建造者</p>
 *
 * @param <S> 短信对象
 * @param <C> 短信配置对象
 * @author zoutongkun
 */
public interface SmsProviderFactory<S extends SmsClient, C extends SmsProviderConfig> {

    /**
     * 创建短信实现对象
     *
     * @param c 短信配置对象，这里其实就是要总接口SmsApi去接即可！
     * @return 短信实现对象
     */
    S createSmsClient(C c);

    /**
     * 获取当前短信服务提供商的配置类
     *
     * @return 配置类
     */
    Class<C> getConfigClass();

    /**
     * 获取当前短信服务的供应商名称
     *
     * @return 供应商
     */
    String getSupplier();

}
