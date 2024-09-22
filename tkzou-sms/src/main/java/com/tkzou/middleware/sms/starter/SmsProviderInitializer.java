package com.tkzou.middleware.sms.starter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.sms.common.constant.CommonConstant;
import com.tkzou.middleware.sms.core.SmsClientFactory;
import com.tkzou.middleware.sms.core.SmsProviderFactoryHolder;
import com.tkzou.middleware.sms.core.interceptor.SmsInvocationHandler;
import com.tkzou.middleware.sms.core.interceptor.strategy.SpringSmsSendInterceptor;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.provider.factory.SmsProviderFactory;
import com.tkzou.middleware.sms.provider.impl.aliyun.AlibabaClientFactory;
import com.tkzou.middleware.sms.provider.impl.huawei.HuaweiClientFactory;
import com.tkzou.middleware.sms.provider.impl.tencent.TencentClientFactory;
import com.tkzou.middleware.sms.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;


/**
 * 初始化当前项目--核心类！
 * 也是当前项目启动的入口
 * 使用springboot的事件监听机制实现初始化！
 * 这里没有通过@configuration注入，而是在SmsAutoConfiguration中注入到ioc容器中，常规套路
 *
 * @author zoutongkun
 */
@Slf4j
@AllArgsConstructor
public class SmsProviderInitializer implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 用户自行实现的短信对象工厂集合,是在注入bean时依赖注入的，具体则是读取配置类中的值，当前没有，为null！
     */
    private List<SmsProviderFactory<? extends SmsClient, ? extends SmsProviderConfig>> customerFactoryList;

    private final SmsCommonConfig smsCommonConfig;
    private final Map<String, Map<String, Object>> blends;

    /**
     * 初始化
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        //注册默认支持的服务提供商，实际上就是咱们自己写的
        this.registerDefaultFactory();
        // 注册用户实现的短信对象工厂，当前没有，为null
        SmsProviderFactoryHolder.register(customerFactoryList);
        // 解析供应商配置
        for (String configId : blends.keySet()) {
            Map<String, Object> configMap = blends.get(configId);
            Object supplierObj = configMap.get(CommonConstant.SUPPLIER_KEY);
            String supplier = supplierObj == null ? "" : String.valueOf(supplierObj);
            supplier = StrUtil.isEmpty(supplier) ? configId : supplier;
            //拿到对应的工厂类，由他来创建具体的client，并注册/保存到本地maP
            SmsProviderFactory<SmsClient, SmsProviderConfig> providerFactory = (SmsProviderFactory<SmsClient,
                    SmsProviderConfig>) SmsProviderFactoryHolder.choose(supplier);
            if (providerFactory == null) {
                log.warn("创建\"{}\"的短信服务失败，未找到供应商为\"{}\"的服务", configId, supplier);
                continue;
            }
            configMap.put("config-id", configId);
            StringUtil.replaceKeysSeparator(configMap, "-", "_");
            JSONObject configJson = new JSONObject(configMap);
            SmsProviderConfig supplierConfig = JSONUtil.toBean(configJson, providerFactory.getConfigClass());
            //注册smsClient到本地map
            if (Boolean.TRUE.equals(smsCommonConfig.getRestricted())) {
                SmsClientFactory.createRestrictedSmsBlend(supplierConfig);
            } else {
                SmsClientFactory.createSmsBlend(supplierConfig);
            }
        }

        //注册短信拦截实现
        //也可以直接使用@component注入吧?
        SmsInvocationHandler.setSendInterceptor(new SpringSmsSendInterceptor(smsCommonConfig));
    }

    /**
     * 注册所有默认支持的工厂实例（而不是具体的实例对象！）
     * 这里是不需要和短信服务商交互的，且通过该工厂获取一个具体的实例对象时也不需要，
     * 需要交互的是在实际发送短信时！
     * 这就非常巧妙地解耦了配置参数的合法性校验了。
     */
    private void registerDefaultFactory() {
        SmsProviderFactoryHolder.register(AlibabaClientFactory.getInstance());
        SmsProviderFactoryHolder.register(TencentClientFactory.getInstance());
        SmsProviderFactoryHolder.register(HuaweiClientFactory.getInstance());
    }

}
