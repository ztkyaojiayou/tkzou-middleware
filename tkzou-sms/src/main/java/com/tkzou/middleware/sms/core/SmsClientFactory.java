package com.tkzou.middleware.sms.core;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.sms.common.enums.ProviderTypeEnum;
import com.tkzou.middleware.sms.core.datainterface.SmsCustomerConfig;
import com.tkzou.middleware.sms.core.interceptor.SmsInvocationHandler;
import com.tkzou.middleware.sms.core.loadbalance.SmsLoadBalancer;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.BaseSmsProviderConfig;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.provider.factory.SmsProviderFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * SmsClientFactory 使用入口！！！
 * 集中了项目在初始化时注入的各类SmsClientFactory
 * <p>构造工厂，用于获取一个厂商的短信实现对象
 * 在调用对应厂商的短信发送方法前，请先确保你的配置已经实现，否则无法发送该厂商对应的短信，
 * 一般情况下厂商会回执因缺少的配置所造成的的异常，但组件不会处理
 *
 * @author :zoutongkun
 * 2024/4/8  15:55
 **/
public abstract class SmsClientFactory {

    /**
     * 在项目初始化时会注入，具体在SmsProviderInitializer类中实现！
     * <p>框架维护的所有短信服务对象</p>
     * <p>key: configId，短信服务对象的唯一标识</p>
     * <p>value: 短信服务对象</p>
     */
    private final static Map<String, SmsClient> allSupportedBlendMap = new ConcurrentHashMap<>();

    private SmsClientFactory() {
    }

    /**
     * 通过供应商标识获取单个短信服务对象
     * <p>当供应商有多个短信服务对象时无法保证获取顺序</p>
     *
     * @param provider 供应商标识
     * @return 返回短信服务对象。如果未找到则返回null
     */
    public static SmsClient getByType(ProviderTypeEnum provider) {
        if (ObjectUtil.isEmpty(provider)) {
            throw new SmsException("供应商标识不能为空");
        }
        return allSupportedBlendMap.values().stream().filter(smsBlend -> provider.getCode().equals(smsBlend.getProviderName())).findFirst().orElseThrow(() -> new SmsException("不支持对应供应商的短信服务对象"));
    }

    /**
     * renderWithRestricted
     * <p>  构建smsBlend对象的代理对象
     *
     * @author :zoutongkun
     */
    private static SmsClient renderWithRestricted(SmsClient sms) {
        SmsInvocationHandler smsInvocationHandler = SmsInvocationHandler.create(sms);
        return (SmsClient) Proxy.newProxyInstance(sms.getClass().getClassLoader(), new Class[]{SmsClient.class},
                smsInvocationHandler);
    }

    /**
     * 通过负载均衡服务获取短信服务对象
     *
     * @return 返回短信服务列表
     */
    public static SmsClient chooseSmsBlend() {
        return SmsLoadBalancer.getInstance().getLoadServer();
    }

    /**
     * 通过configId获取短信服务对象
     *
     * @param configId 唯一标识
     * @return 返回短信服务对象。如果未找到则返回null
     */
    public static SmsClient getSmsBlend(String configId) {
        return allSupportedBlendMap.get(configId);
    }

    /**
     * 通过供应商标识获取短信服务对象列表
     *
     * @param provider 供应商标识
     * @return 返回短信服务对象列表。如果未找到则返回空列表
     */
    public static List<SmsClient> getListBySupplier(String provider) {
        List<SmsClient> list;
        if (StrUtil.isEmpty(provider)) {
            throw new SmsException("供应商标识不能为空");
        }
        list = allSupportedBlendMap.values().stream().filter(smsBlend -> provider.equals(smsBlend.getProviderName())).collect(Collectors.toList());
        return list;
    }

    /**
     * 获取全部短信服务对象
     *
     * @return 短信服务对象列表
     */
    public static List<SmsClient> getAllSupportedClient() {
        return new ArrayList<>(allSupportedBlendMap.values());
    }

//-----------------------------------注册--------------------------------------------

    /**
     * 注册短信服务对象
     *
     * @param smsClient 短信服务对象
     */
    public static void register(SmsClient smsClient) {
        if (smsClient == null) {
            throw new SmsException("短信服务对象不能为空");
        }
        allSupportedBlendMap.put(smsClient.getConfigId(), smsClient);
        SmsLoadBalancer.add(smsClient, 1);
    }

    /**
     * 注册短信服务对象
     *
     * @param smsClient 短信服务对象
     */
    public static void register(SmsClient smsClient, Integer weight) {
        if (smsClient == null) {
            throw new SmsException("短信服务对象不能为空");
        }
        allSupportedBlendMap.put(smsClient.getConfigId(), smsClient);
        SmsLoadBalancer.add(smsClient, weight);
    }

    /**
     * 以configId为标识，当短信服务对象不存在时，进行注册
     *
     * @param smsClient 短信服务对象
     * @return 是否注册成功
     * <p>当对象不存在时，进行注册并返回true</p>
     * <p>当对象已存在时，返回false</p>
     */
    public static boolean registerIfAbsent(SmsClient smsClient) {
        if (smsClient == null) {
            throw new SmsException("短信服务对象不能为空");
        }
        String configId = smsClient.getConfigId();
        if (allSupportedBlendMap.containsKey(configId)) {
            return false;
        }
        allSupportedBlendMap.put(configId, smsClient);
        SmsLoadBalancer.add(smsClient, 1);
        return true;
    }

    /**
     * registerIfAbsent
     * <p> 以configId为标识，当短信服务对象不存在时，进行注册。并添加至系统的负载均衡器
     *
     * @param smsClient 短信服务对象
     * @param weight    权重
     * @return 是否注册成功
     * <p>当对象不存在时，进行注册并返回true</p>
     * <p>当对象已存在时，返回false</p>
     * @author :zoutongkun
     */
    public static boolean registerIfAbsent(SmsClient smsClient, Integer weight) {
        if (smsClient == null) {
            throw new SmsException("短信服务对象不能为空");
        }
        String configId = smsClient.getConfigId();
        if (allSupportedBlendMap.containsKey(configId)) {
            return false;
        }
        allSupportedBlendMap.put(configId, smsClient);
        SmsLoadBalancer.add(smsClient, weight);
        return true;
    }

    /**
     * 注销短信服务对象
     * <p>与此同时会注销掉负载均衡器中已经存在的对象</p>
     *
     * @param configId 标识
     * @return 是否注销成功
     * <p>当configId存在时，进行注销并返回true</p>
     * <p>当configId不存在时，返回false</p>
     */
    public static boolean unregister(String configId) {
        SmsClient blend = allSupportedBlendMap.remove(configId);
        SmsLoadBalancer.getInstance().removeLoadServer(blend);
        return blend != null;
    }

    /**
     * createSmsBlend
     * <p>创建各个厂商的实现类
     *
     * @param config 短信配置
     * @author :zoutongkun
     */
    public static void createSmsBlend(SmsProviderConfig config) {
        SmsClient smsClient = doCreate(config);
        register(smsClient);
    }


    /**
     * createSmsBlend
     * <p>通过配置读取接口创建某个短信实例
     * <p>该方法创建的短信实例将会交给框架进行托管，后续可以通过getSmsBlend获取
     * <p>该方法会直接调用接口实现
     *
     * @param smsCustomerConfig 读取额外配置接口
     * @param configId      配置ID
     * @author :zoutongkun
     */
    public static void createSmsBlend(SmsCustomerConfig smsCustomerConfig, String configId) {
        BaseSmsProviderConfig supplierConfig = smsCustomerConfig.getSupplierConfig(configId);
        SmsClient smsClient = doCreate(supplierConfig);
        register(smsClient);
    }

    /**
     * createSmsBlend
     * <p>通过配置读取接口创建全部短信实例
     * <p>该方法创建的短信实例将会交给框架进行托管，后续可以通过getSmsBlend获取
     * <p>该方法会直接调用接口实现
     *
     * @param smsCustomerConfig 读取额外配置接口
     * @author :zoutongkun
     */
    public static void createSmsBlend(SmsCustomerConfig smsCustomerConfig) {
        List<BaseSmsProviderConfig> supplierConfigList = smsCustomerConfig.getSupplierConfigList();
        supplierConfigList.forEach(supplierConfig -> {
            SmsClient smsClient = doCreate(supplierConfig);
            register(smsClient);
        });
    }

    /**
     * createRestrictedSmsBlend
     * <p> 创建一个指定厂商开启短信拦截后的实例，拦截的参数取决于配置参数
     *
     * @param config 短信配置
     * @author :zoutongkun
     */
    public static void createRestrictedSmsBlend(SmsProviderConfig config) {
        SmsClient smsClient = doCreate(config);
        smsClient = renderWithRestricted(smsClient);
        register(smsClient);
    }

    /**
     * createRestrictedSmsBlend
     * <p>通过配置读取接口创建某个开启短信拦截后的短信实例
     * <p>该方法创建的短信实例将会交给框架进行托管，后续可以通过getSmsBlend获取
     * <p>该方法会直接调用接口实现
     *
     * @param smsCustomerConfig 读取额外配置接口
     * @param configId      配置ID
     * @author :zoutongkun
     */
    public static void createRestrictedSmsBlend(SmsCustomerConfig smsCustomerConfig, String configId) {
        BaseSmsProviderConfig supplierConfig = smsCustomerConfig.getSupplierConfig(configId);
        SmsClient smsClient = doCreate(supplierConfig);
        smsClient = renderWithRestricted(smsClient);
        register(smsClient);
    }

    /**
     * createRestrictedSmsBlend
     * <p>通过配置读取接口创建全部开启短信拦截后的短信实例
     * <p>该方法创建的短信实例将会交给框架进行托管，后续可以通过getSmsBlend获取
     * <p>该方法会直接调用接口实现
     *
     * @param smsCustomerConfig 读取额外配置接口
     * @author :zoutongkun
     */
    public static void createRestrictedSmsBlend(SmsCustomerConfig smsCustomerConfig) {
        List<BaseSmsProviderConfig> supplierConfigList = smsCustomerConfig.getSupplierConfigList();
        supplierConfigList.forEach(supplierConfig -> {
            SmsClient smsClient = doCreate(supplierConfig);
            smsClient = renderWithRestricted(smsClient);
            register(smsClient);
        });
    }

    /**
     * 具体创建逻辑
     *
     * @param config
     * @return
     */
    private static SmsClient doCreate(SmsProviderConfig config) {
        //本质就是去已经初始化了的工厂map中取即可！
        SmsProviderFactory factory = SmsProviderFactoryHolder.choose(config.getSupplier());
        //判断一下
        if (factory == null) {
            throw new SmsException("不支持当前供应商配置");
        }
        //再通过该工厂去生成一个具体的用于发送短信的实例对象，也即client
        return factory.createSmsClient(config);
    }
}
