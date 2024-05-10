package com.tkzou.middleware.sms.core.interceptor;

import com.tkzou.middleware.sms.exception.SmsException;

/**
 * 短信拦截处理接口，其实现类又可以看成是不同的策略！！！
 * 用于增强业务逻辑，比如限制一个手机号的单日发送限制等
 * 最佳实践就是配合动态代理模式使用！！！
 *
 * @author zoutongkun
 */
public interface SmsSendInterceptor {

    /**
     * 拦截校验过程
     *
     * @param phone 手机号
     * @return
     */
    SmsException process(String phone);

}
