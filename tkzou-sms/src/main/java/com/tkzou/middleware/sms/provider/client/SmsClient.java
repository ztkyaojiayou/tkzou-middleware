package com.tkzou.middleware.sms.provider.client;


import com.tkzou.middleware.sms.common.SmsResponse;
import com.tkzou.middleware.sms.core.callback.CallBack;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * SmsClient
 * <p> 核心通用接口，定义了发送短信的方法
 * 这里面不涉及泛型，本来最有可能使用泛型的地方就是请求参数，
 * 但由于我们使用发送http请求的方式，因此全部使用map封装即可，
 * 最终都会被转换成json发送！
 *
 * @author :zoutongkun
 * 2024/5/16  16:03
 **/
public interface SmsClient {
    /**
     * 获取短信服务提供商配置id
     *
     * @return
     */
    String getConfigId();

    /**
     * 获取短信服务提供商名称
     * 如阿里云短信
     *
     * @return
     */
    String getProviderName();

    /**
     * 发送固定消息模板短信
     *
     * @param phone
     * @param message
     * @return
     */
    SmsResponse sendMessage(String phone, String message);

    /**
     * 使用自定义模板发送短信
     *
     * @param phone
     * @param templateId
     * @param messages
     * @return
     */
    SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages);

    /**
     * 使用默认模板群发短信
     *
     * @param phones
     * @param message
     * @return
     */
    SmsResponse massTexting(List<String> phones, String message);

    /**
     * 群发短信
     *
     * @param phones
     * @param templateId
     * @param messages
     * @return
     */
    SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages);

    /**
     * 异步短信发送，固定消息模板短信
     *
     * @param phone
     * @param message
     * @param callBack
     */
    void sendMessageAsync(String phone, String message, CallBack callBack);

    /**
     * 异步发送短信，不关注发送结果
     *
     * @param phone
     * @param message
     */
    void sendMessageAsync(String phone, String message);

    /**
     * 异步短信发送，使用自定义模板发送短信
     *
     * @param phone
     * @param templateId
     * @param messages
     * @param callBack
     */
    void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages, CallBack callBack);

    /**
     * 异步短信发送，使用自定义模板发送短信，不关注发送结果
     *
     * @param phone
     * @param templateId
     * @param messages
     */
    void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages);

    /**
     * 使用固定模板发送延时短信
     * 注意：只是延迟一下，而不是定时发送！
     *
     * @param phone
     * @param message
     * @param delayedTime
     */
    void delayedMessage(String phone, String message, Long delayedTime);

    /**
     * 使用自定义模板发送定时短信
     *
     * @param phone
     * @param templateId
     * @param messages
     * @param delayedTime
     */
    void delayedMessage(String phone, String templateId, LinkedHashMap<String, String> messages, Long delayedTime);

    /**
     * 群发延迟短信
     *
     * @param phones
     * @param message
     * @param delayedTime
     */
    void delayMassTexting(List<String> phones, String message, Long delayedTime);

    /**
     * 使用自定义模板发送群体延迟短信
     *
     * @param phones
     * @param templateId
     * @param messages
     * @param delayedTime
     */
    void delayMassTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages, Long delayedTime);

}
