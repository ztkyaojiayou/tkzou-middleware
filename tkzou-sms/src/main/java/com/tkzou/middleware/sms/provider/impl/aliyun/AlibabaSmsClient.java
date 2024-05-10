package com.tkzou.middleware.sms.provider.impl.aliyun;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.sms.common.SmsResponse;
import com.tkzou.middleware.sms.common.constant.CommonConstant;
import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.AbstractSmsClient;
import com.tkzou.middleware.sms.provider.impl.aliyun.config.AlibabaSmsConfig;
import com.tkzou.middleware.sms.provider.impl.aliyun.util.AliyunUtil;
import com.tkzou.middleware.sms.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 阿里云短信实现
 * 此时就会在泛型参数处传入阿里云短信自己的配置类
 * 之后再通过一个工厂类来专门生产该实现类对象！
 * 其实也可以叫策略模式，理解即可，模式名称无所谓
 * 不直接注入到ioc容器
 *
 * @author zoutongkun
 */
@Slf4j
public class AlibabaSmsClient extends AbstractSmsClient<AlibabaSmsConfig> {

    private int retry = 0;

    /**
     * AlibabaSmsImpl
     * <p>构造器，用于构造短信实现模块
     * 需要传入必要参数，然后通过super来调用父类的构造器即可，因为这些参数都在父类呀！
     *
     * @author :zoutongkun
     */
    public AlibabaSmsClient(AlibabaSmsConfig config, Executor pool) {
        super(config, pool);
    }

    /**
     * AlibabaSmsImpl
     * <p>构造器，用于构造短信实现模块
     */
    public AlibabaSmsClient(AlibabaSmsConfig config) {
        super(config);
    }

    @Override
    public String getProviderName() {
        return SmsSupplierConstant.ALIBABA;
    }

    @Override
    public SmsResponse sendMessage(String phone, String message) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //此时，这个方法获取的就是当前类的泛型类，即阿里云自己的配置类！
//        AlibabaSmsConfig config = getConfig();
        map.put(getConfig().getTemplateName(), message);
        return sendMessage(phone, getConfig().getTemplateId(), map);
    }

    @Override
    public SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages) {
        //转为json发送
        String messageStr = JSONUtil.toJsonStr(messages);
        return getSmsResponse(phone, messageStr, templateId);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String message) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(getConfig().getTemplateName(), message);
        return massTexting(phones, getConfig().getTemplateId(), map);
    }

    @Override
    public SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        String messageStr = JSONUtil.toJsonStr(messages);
        return getSmsResponse(StringUtil.arrayToString(phones), messageStr, templateId);
    }

    /**
     * 发送短信请求
     * 这些逻辑就不重要了，大致了解一下即可！
     *
     * @param phone
     * @param message
     * @param templateId
     * @return
     */
    private SmsResponse getSmsResponse(String phone, String message, String templateId) {
        String requestUrl;
        String paramStr;
        try {
            //从配置文件中获取用户名密码等必要信息
            //再组装请求
            requestUrl = AliyunUtil.buildSendSmsRequestUrl(getConfig(), message, phone, templateId);
            //封装参数
            paramStr = AliyunUtil.buildRequestParamBody(getConfig(), phone, message, templateId);
        } catch (Exception e) {
            log.error("aliyun send message error", e);
            throw new SmsException(e.getMessage());
        }
        log.debug("requestUrl {}", requestUrl);
        try {
            Map<String, String> headers = new LinkedHashMap<>(1);
            headers.put("Content-Type", CommonConstant.FROM_URLENCODED);
            //发送请求，获取结果
            SmsResponse smsResponse = getResponse(httpUtil.postJson(requestUrl, headers, paramStr));
            //成功或失败但重试次数到达了配置的最大值时就返回
            if (smsResponse.isSuccess() || retry == getConfig().getMaxRetries()) {
                //记得重试次数置零！
                retry = 0;
                return smsResponse;
            }
            //否则重试！
            return requestRetry(phone, message, templateId);
        } catch (SmsException e) {
            return requestRetry(phone, message, templateId);
        }
    }

    /**
     * 重新发送
     *
     * @param phone
     * @param message
     * @param templateId
     * @return
     */
    private SmsResponse requestRetry(String phone, String message, String templateId) {
        //休眠一下再重试
        httpUtil.safeSleep(getConfig().getRetryInterval());
        //重试次数维护
        retry++;
        log.warn("短信第 {" + retry + "} 次重新发送");
        //开始重试
        return getSmsResponse(phone, message, templateId);
    }

    /**
     * 解析返回结果
     *
     * @param resJson
     * @return
     */
    private SmsResponse getResponse(JSONObject resJson) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess("OK".equals(resJson.getStr("Code")));
        smsResponse.setData(resJson);
        smsResponse.setConfigId(getConfigId());
        return smsResponse;
    }

}