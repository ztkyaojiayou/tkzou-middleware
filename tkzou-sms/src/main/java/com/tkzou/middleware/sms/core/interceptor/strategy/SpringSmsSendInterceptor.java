package com.tkzou.middleware.sms.core.interceptor.strategy;

import com.tkzou.middleware.sms.core.dao.SmsDao;
import com.tkzou.middleware.sms.core.interceptor.SmsSendInterceptor;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.starter.SmsCommonConfig;
import com.tkzou.middleware.sms.util.SpringUtil;
import com.tkzou.middleware.sms.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 限流策略2
 *
 * @author zoutongkun
 */
@Slf4j
@AllArgsConstructor
public class SpringSmsSendInterceptor implements SmsSendInterceptor {
    private static final Long minTimer = 60 * 1000L;
    private static final Long accTimer = 24 * 60 * 60 * 1000L;
    private static final String REDIS_KEY = "sms:restricted:";

    private SmsCommonConfig smsCommonConfig;

    @Override
    public SmsException process(String phone) {
        SmsDao smsDao = SpringUtil.getBean(SmsDao.class);
        // 每日最大发送量
        Integer accountMax = smsCommonConfig.getAccountMax();
        // 每分钟最大发送量
        Integer minuteMax = smsCommonConfig.getMinuteMax();
        // 1.处理每日限制
        // 先检查是否配置了每日限制
        if (StringUtil.isNotEmpty(accountMax)) {
            //使用redis存储每个手机号当天发送的数量，常规玩法！
            Integer curCnt = (Integer) smsDao.get(REDIS_KEY + phone + "max");
            //为空时，表示第一次发送
            if (StringUtil.isEmpty(curCnt)) {
                smsDao.set(REDIS_KEY + phone + "max", 1, accTimer / 1000);
                //若大于指定的上限，则抛异常，不再发送！
            } else if (curCnt >= accountMax) {
                log.info("The phone:" + phone + ",number of short messages reached the maximum today");
                return new SmsException("The phone:" + phone + ",number of short messages reached the maximum today");
            } else {
                //否则，维护一下当前手机号发送的数量即可
                smsDao.set(REDIS_KEY + phone + "max", curCnt + 1, accTimer / 1000);
            }
        }
        //2.处理每分钟最大限制，同上
        // 先检查是否配置了每分钟最大限制
        if (StringUtil.isNotEmpty(minuteMax)) {
            Integer o = (Integer) smsDao.get(REDIS_KEY + phone);
            if (StringUtil.isNotEmpty(o)) {
                if (o < minuteMax) {
                    smsDao.set(REDIS_KEY + phone, o + 1, minTimer / 1000);
                } else {
                    log.info("The phone:" + phone + ",number of short messages reached the maximum today");
                    return new SmsException("The phone:", phone + " Text messages are sent too often！");
                }
            } else {
                smsDao.set(REDIS_KEY + phone, 1, minTimer / 1000);
            }
        }
        return null;
    }
}
