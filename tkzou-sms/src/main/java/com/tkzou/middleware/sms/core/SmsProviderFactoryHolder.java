package com.tkzou.middleware.sms.core;

import cn.hutool.core.collection.CollUtil;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.provider.factory.SmsProviderFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 供应商工厂持有者
 * 策略模式，老生常谈了
 *
 * @author zoutongkun
 * @since 3.0.0
 */
public class SmsProviderFactoryHolder {
    /**
     * 保存所有的短信服务提供商工厂对象
     * key：短信服务提供商名称，value：具体的工厂对象
     */
    private static final Map<String, SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig>> SmsProviderFactoryMap = new ConcurrentHashMap<>();

    /**
     * 注册单个短信服务提供商工厂
     *
     * @param factory
     */
    public static void register(SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig> factory) {
        if (factory == null) {
            throw new SmsException("注册供应商工厂失败，工厂实例不能为空");
        }
        SmsProviderFactoryMap.put(factory.getSupplier(), factory);
    }

    /**
     * 批量注册
     *
     * @param factoryList
     */
    public static void register(List<SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig>> factoryList) {
        if (CollUtil.isEmpty(factoryList)) {
            return;
        }
        for (SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig> factory : factoryList) {
            if (factory == null) {
                continue;
            }
            register(factory);
        }
    }

    /**
     * 根据服务提供商类型获取对应的工厂对象
     *
     * @param provider
     * @return
     */
    public static SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig> choose(String provider) {
        return SmsProviderFactoryMap.getOrDefault(provider, null);
    }

}
