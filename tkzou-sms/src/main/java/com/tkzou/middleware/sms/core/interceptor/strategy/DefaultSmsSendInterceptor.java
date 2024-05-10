package com.tkzou.middleware.sms.core.interceptor.strategy;

import com.tkzou.middleware.sms.core.dao.SmsDao;
import com.tkzou.middleware.sms.core.interceptor.SmsSendInterceptor;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.starter.SmsCommonConfig;
import com.tkzou.middleware.sms.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 默认限流策略
 *
 * @author zoutongkun
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultSmsSendInterceptor implements SmsSendInterceptor {
    static Long minTimer = 60 * 1000L;
    static Long accTimer = 24 * 60 * 60 * 1000L;

    @Autowired
    private SmsCommonConfig smsCommonConfig;

    /**
     * 缓存实例
     */
    @Setter
    private SmsDao smsDao;

    @Override
    public SmsException process(String phone) {
        if (Objects.isNull(smsDao)) {
            throw new SmsException("The dao tool could not be found");
        }
        // 每日最大发送量
        Integer accountMax = smsCommonConfig.getAccountMax();
        // 每分钟最大发送量
        Integer minuteMax = smsCommonConfig.getMinuteMax();
        // 是否配置了每日限制
        if (StringUtil.isNotEmpty(accountMax)) {
            Integer i = (Integer) smsDao.get(phone + "max");
            if (StringUtil.isEmpty(i)) {
                smsDao.set(phone + "max", 1, accTimer);
            } else if (i >= accountMax) {
                log.info("The phone:" + phone + ",number of short messages reached the maximum today");
                return new SmsException("The phone:" + phone + ",number of short messages reached the maximum today");
            } else {
                smsDao.set(phone + "max", i + 1, accTimer);
            }
        }
        // 是否配置了每分钟最大限制
        if (StringUtil.isNotEmpty(minuteMax)) {
            Integer o = (Integer) smsDao.get(phone);
            if (StringUtil.isNotEmpty(o)) {
                if (o < minuteMax) {
                    smsDao.set(phone, o + 1, minTimer);
                } else {
                    log.info("The phone:" + phone + " Text messages are sent too often！");
                    return new SmsException("The phone:", phone + " Text messages are sent too often！");
                }
            } else {
                smsDao.set(phone, 1, minTimer);
            }
        }
        return null;
    }
}
