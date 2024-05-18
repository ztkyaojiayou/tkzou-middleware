package com.tkzou.middleware.sms.core.interceptor;

import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 代理smsApi对象时的代理逻辑
 *
 * @author zoutongkun
 */
@Slf4j
@Component
public class SmsInvocationHandler implements InvocationHandler {
    public static final String SEND_MESSAGE = "sendMessage";
    public static final String MASS_TEXTING = "massTexting";
    private final SmsClient smsClient;
    @Autowired
    private SmsSendInterceptor defaultSmsSendInterceptor;
    /**
     * 这里使用了默认的限流处理器
     * 但提供了set方法动态设置
     */
    private static SmsSendInterceptor smsSendInterceptor;

    public SmsInvocationHandler(SmsClient smsClient) {
        this.smsClient = smsClient;
    }

    @PostConstruct
    void init() {
        smsSendInterceptor = defaultSmsSendInterceptor;
    }

    /**
     * 构造者
     *
     * @param smsClient
     * @return
     */
    public static SmsInvocationHandler create(SmsClient smsClient) {
        return new SmsInvocationHandler(smsClient);
    }

    /**
     * 代理逻辑
     * 代理的是smsApi对象
     * 该对象的每个方法在执行时都会改为调用该方法
     *
     * @param proxy  代理对象
     * @param method 被执行的方法
     * @param args   方法参数，是个数组，按照方法定义的顺序取即可！
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (SEND_MESSAGE.equals(method.getName()) || MASS_TEXTING.equals(method.getName())) {
            //取手机号作为参数
            String phone = (String) args[0];
            //增强逻辑
            SmsException smsException = smsSendInterceptor.process(phone);
            if (!Objects.isNull(smsException)) {
                throw smsException;
            }
        }
        //再执行原始方法
        result = method.invoke(smsClient, args);
        return result;
    }

    /**
     * 设置 短信发送拦截器
     */
    public static void setSendInterceptor(SmsSendInterceptor sendInterceptor) {
        smsSendInterceptor = sendInterceptor;
    }
}
