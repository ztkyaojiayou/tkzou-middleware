package com.tkzou.middleware.sms.core.load;

import com.tkzou.middleware.sms.provider.client.SmsClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送短信的负载
 * 使用场景: 使用多个client保证短信发送高可用,每个client配置一个权重
 *
 * @author zoutongkun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsServer {
    /**
     * 短信发送的client
     */
    private SmsClient smsClient;
    /**
     * 权重
     */
    private int weight;
    /**
     * 当前权重
     */
    private int currentWeight;

    /**
     * 构造者
     *
     * @param smsClient
     * @param weight
     * @param currentWeight
     * @return
     */
    protected static SmsServer create(SmsClient smsClient, int weight, int currentWeight) {
        return new SmsServer(smsClient, weight, currentWeight);
    }
}
