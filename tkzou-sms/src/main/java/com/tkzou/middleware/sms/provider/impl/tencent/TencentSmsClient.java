package com.tkzou.middleware.sms.provider.impl.tencent;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.tkzou.middleware.sms.common.SmsResponse;
import com.tkzou.middleware.sms.common.constant.CommonConstant;
import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.AbstractSmsClient;
import com.tkzou.middleware.sms.provider.impl.tencent.config.TencentSmsConfig;
import com.tkzou.middleware.sms.provider.impl.tencent.util.TencentUtil;
import com.tkzou.middleware.sms.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 腾讯云短信服务实现
 * 不直接注入到ioc容器
 *
 * @author zoutongkun
 */
@Slf4j
public class TencentSmsClient extends AbstractSmsClient<TencentSmsConfig> {

    private int retry = 0;

    public TencentSmsClient(TencentSmsConfig tencentSmsConfig, Executor pool) {
        super(tencentSmsConfig, pool);
    }

    public TencentSmsClient(TencentSmsConfig tencentSmsConfig) {
        super(tencentSmsConfig);
    }

    @Override
    public String getProviderName() {
        return SmsSupplierConstant.TENCENT;
    }

    @Override
    public SmsResponse sendMessage(String phone, String message) {
        String[] split = message.split("&");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < split.length; i++) {
            map.put(String.valueOf(i), split[i]);
        }
        return sendMessage(phone, getConfig().getTemplateId(), map);
    }

    @Override
    public SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            list.add(entry.getValue());
        }
        String[] s = new String[list.size()];
        return getSmsResponse(new String[]{StrUtil.addPrefixIfNot(phone, "+86")}, list.toArray(s), templateId);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String message) {
        String[] split = message.split("&");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < split.length; i++) {
            map.put(String.valueOf(i), split[i]);
        }
        return massTexting(phones, getConfig().getTemplateId(), map);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            list.add(entry.getValue());
        }
        String[] s = new String[list.size()];
        return getSmsResponse(StringUtil.listToArray(phones), list.toArray(s), templateId);
    }

    private SmsResponse getSmsResponse(String[] phones, String[] messages, String templateId) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signature;
        try {
            signature = TencentUtil.buildSignature(this.getConfig(), templateId, messages, phones, timestamp);
        } catch (Exception e) {
            log.error("tencent send message error", e);
            throw new SmsException(e.getMessage());
        }
        Map<String, String> headsMap = TencentUtil.buildHeadsMap(signature, timestamp, getConfig().getAction(),
                getConfig().getVersion(), getConfig().getTerritory(), getConfig().getRequestUrl());
        Map<String, Object> requestBody = TencentUtil.buildRequestBody(phones, getConfig().getAccessKeyId(),
                getConfig().getSignature(), templateId, messages);
        String url = CommonConstant.HTTPS_PREFIX + getConfig().getRequestUrl();

        try {
            SmsResponse smsResponse = getResponse(httpUtil.postJson(url, headsMap, requestBody));
            if (smsResponse.isSuccess() || retry == getConfig().getMaxRetries()) {
                retry = 0;
                return smsResponse;
            }
            return requestRetry(phones, messages, templateId);
        } catch (SmsException e) {
            return requestRetry(phones, messages, templateId);
        }
    }

    private SmsResponse requestRetry(String[] phones, String[] messages, String templateId) {
        httpUtil.safeSleep(getConfig().getRetryInterval());
        retry++;
        log.warn("短信第 {" + retry + "} 次重新发送");
        return getSmsResponse(phones, messages, templateId);
    }

    private SmsResponse getResponse(JSONObject resJson) {
        SmsResponse smsResponse = new SmsResponse();
        JSONObject response = resJson.getJSONObject("Response");
        String error = response.getStr("Error");
        smsResponse.setSuccess(StrUtil.isBlank(error));
        smsResponse.setData(resJson);
        smsResponse.setConfigId(getConfigId());
        return smsResponse;
    }
}