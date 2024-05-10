package com.tkzou.middleware.sms.core.datainterface;


import com.tkzou.middleware.sms.provider.config.BaseSmsProviderConfig;

import java.util.List;

/**
 * SmsReadConfig
 * <p> 读取配置接口，实现该接口中的方法则可以按照自己的形式进行配置的读取
 * <p>这样只关注最终的配置数据而不关注配置的来源，用户可以自由的选择数据来源的方式</p>
 * <p>该种方式读取配置并非在启动阶段完成，而是在方法第一次调用期间完成</p>
 *
 * @author :zoutongkun
 * 2024/8/1  12:06
 **/
public interface SmsReadConfig {

    /**
     * 通过配置ID获取一个厂商的配置
     *
     * @param configId
     * @return
     */
    BaseSmsProviderConfig getSupplierConfig(String configId);

    /**
     * 获取多个厂商的配置，会同时加载进框架中
     *
     * @return
     */
    List<BaseSmsProviderConfig> getSupplierConfigList();

}
