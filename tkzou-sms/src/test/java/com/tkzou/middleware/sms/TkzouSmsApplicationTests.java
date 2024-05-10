package com.tkzou.middleware.sms;

import com.tkzou.middleware.sms.common.enums.ProviderTypeEnum;
import com.tkzou.middleware.sms.core.SmsClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试类
 */
@SpringBootTest
class TkzouSmsApplicationTests {
    /**
     * 开箱即用
     */
    @Test
    void test() {
        //使用阿里云向此手机号发送短信
        SmsClientFactory.getByType(ProviderTypeEnum.ALIBABA).sendMessage("15288888888", "hello aliyun");
        //使用腾讯云短信向此手机号发送短信
        SmsClientFactory.getByType(ProviderTypeEnum.TENCENT).sendMessage("13966666666", "hello tecent");
        //使用华为短信向此手机号发送短信
        SmsClientFactory.getByType(ProviderTypeEnum.HUAWEI).sendMessage("13766666666", "hello huaweiyun");
    }

}
