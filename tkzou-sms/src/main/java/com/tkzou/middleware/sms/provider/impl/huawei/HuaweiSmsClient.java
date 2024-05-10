package com.tkzou.middleware.sms.provider.impl.huawei;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.tkzou.middleware.sms.common.SmsResponse;
import com.tkzou.middleware.sms.common.constant.CommonConstant;
import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.AbstractSmsClient;
import com.tkzou.middleware.sms.provider.impl.huawei.config.HuaweiSmsConfig;
import com.tkzou.middleware.sms.provider.impl.huawei.util.HuaweiUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * 华为云短信服务实现
 * 不直接注入到ioc容器
 *
 * @author zoutongkun
 */
@Slf4j
public class HuaweiSmsClient extends AbstractSmsClient<HuaweiSmsConfig> {

    private int retry = 0;

    public HuaweiSmsClient(HuaweiSmsConfig config, Executor pool) {
        super(config, pool);
    }

    public HuaweiSmsClient(HuaweiSmsConfig config) {
        super(config);
    }

    @Override
    public String getProviderName() {
        return SmsSupplierConstant.HUAWEI;
    }

    @Override
    public SmsResponse sendMessage(String phone, String message) {
        LinkedHashMap<String, String> mes = new LinkedHashMap<>();
        mes.put(UUID.randomUUID().toString().replaceAll("-", ""), message);
        return sendMessage(phone, getConfig().getTemplateId(), mes);
    }

    @Override
    public SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages) {
        String url = getConfig().getUrl() + CommonConstant.HUAWEI_REQUEST_URL;
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            list.add(entry.getValue());
        }
        String mess = HuaweiUtil.listToString(list);
        String requestBody = HuaweiUtil.buildRequestBody(getConfig().getSender(), phone, templateId, mess, getConfig().getStatusCallBack(), getConfig().getSignature());
        try {
            Map<String, String> headers = new LinkedHashMap<>(3);
            headers.put("Authorization", CommonConstant.HUAWEI_AUTH_HEADER_VALUE);
            headers.put("X-WSSE", HuaweiUtil.buildWsseHeader(getConfig().getAccessKeyId(), getConfig().getAccessKeySecret()));
            headers.put("Content-Type", CommonConstant.FROM_URLENCODED);
            SmsResponse smsResponse = getResponse(httpUtil.postJson(url, headers, requestBody));
            if (smsResponse.isSuccess() || retry == getConfig().getMaxRetries()) {
                retry = 0;
                return smsResponse;
            }
            return requestRetry(phone, templateId, messages);
        } catch (SmsException e) {
            return requestRetry(phone, templateId, messages);
        }
    }

    private SmsResponse requestRetry(String phone, String templateId, LinkedHashMap<String, String> messages) {
        httpUtil.safeSleep(getConfig().getRetryInterval());
        retry++;
        log.warn("短信第 {" + retry + "} 次重新发送");
        return sendMessage(phone, templateId, messages);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String message) {
        return sendMessage(CollUtil.join(phones, ","), message);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        return sendMessage(CollUtil.join(phones, ","), templateId, messages);
    }

    private SmsResponse getResponse(JSONObject resJson) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess("000000".equals(resJson.getStr("code")));
        smsResponse.setData(resJson);
        smsResponse.setConfigId(getConfigId());
        return smsResponse;
    }

}
