package com.tkzou.middleware.sms.provider.impl.aliyun.util;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.tkzou.middleware.sms.common.constant.CommonConstant;
import com.tkzou.middleware.sms.provider.impl.aliyun.config.AlibabaSmsConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zoutongkun
 * @date 2024/4/20 16:55
 */
public class AliyunUtil {

    /**
     * 加密方式
     */
    private static final String ALGORITHM = "HMAC-SHA1";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * 构建发送短信的请求体
     *
     * @param alibabaConfig
     * @param message
     * @param phone
     * @param templateId
     * @return
     * @throws Exception
     */
    public static String buildSendSmsRequestUrl(AlibabaSmsConfig alibabaConfig, String message, String phone, String templateId) throws Exception {
        // 这里一定要设置GMT时区
        SDF.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Map<String, String> paras = new HashMap<>();
        // 1. 公共请求参数
        paras.put("SignatureMethod", ALGORITHM);
        paras.put("SignatureNonce", UUID.randomUUID().toString());
        paras.put("AccessKeyId", alibabaConfig.getAccessKeyId());
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", SDF.format(new Date()));
        paras.put("Format", "JSON");
        paras.put("Action", alibabaConfig.getAction());
        paras.put("Version", alibabaConfig.getVersion());
        paras.put("RegionId", alibabaConfig.getRegionId());
        // 2. 业务API参数
        Map<String, String> paramMap = buildParamMap(alibabaConfig, phone, message, templateId);
        // 3. 参数KEY排序
        Map<String, String> sortParas = new TreeMap<>(paras);
        sortParas.putAll(paramMap);
        // 4. 构造待签名的字符串
        Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(sortParas.get(key)));
        }

        String stringToSign = "POST" + "&" +
                specialUrlEncode("/") + "&" +
                specialUrlEncode(sortQueryStringTmp.substring(1));
        String signature = sign(alibabaConfig.getAccessKeySecret() + "&", stringToSign);
        // 5. 生成请求的url参数
        StringBuilder sortQueryString = new StringBuilder();
        it = paras.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryString.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        // 6.生成合法请求URL
        return CommonConstant.HTTPS_PREFIX + alibabaConfig.getRequestUrl() + "/?Signature=" + specialUrlEncode(signature) + sortQueryString;
    }

    /**
     * url编码
     */
    private static String specialUrlEncode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~");
    }

    /**
     * 生成签名
     *
     * @param accessSecret accessSecret
     * @param stringToSign 待生成签名的字符串
     */
    private static String sign(String accessSecret, String stringToSign) {
        HMac hMac = new HMac(HmacAlgorithm.HmacSHA1, accessSecret.getBytes());
        return hMac.digestBase64(stringToSign, StandardCharsets.UTF_8, false);
    }

    /**
     * 生成请求body参数
     *
     * @param alibabaConfig 配置数据
     * @param phone         手机号
     * @param message       短信内容
     * @param templateId    模板id
     */
    public static Map<String, String> buildParamMap(AlibabaSmsConfig alibabaConfig, String phone, String message, String templateId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("PhoneNumbers", phone);
        paramMap.put("SignName", alibabaConfig.getSignature());
        paramMap.put("TemplateParam", message);
        paramMap.put("TemplateCode", templateId);
        return paramMap;
    }

    /**
     * 生成请求参数body字符串
     *
     * @param alibabaConfig
     * @param phone
     * @param message
     * @param templateId
     */
    public static String buildRequestParamBody(AlibabaSmsConfig alibabaConfig, String phone, String message, String templateId) throws Exception {
        Map<String, String> paramMap = buildParamMap(alibabaConfig, phone, message, templateId);
        StringBuilder sortQueryString = new StringBuilder();
        for (String key : paramMap.keySet()) {
            sortQueryString.append("&").append(specialUrlEncode(key)).append("=")
                    .append(specialUrlEncode(paramMap.get(key)));
        }
        return sortQueryString.substring(1);
    }

}
