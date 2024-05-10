package com.tkzou.middleware.sms.provider.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.sms.common.SmsResponse;
import com.tkzou.middleware.sms.core.callback.CallBack;
import com.tkzou.middleware.sms.exception.SmsException;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.starter.SmsExecutor;
import com.tkzou.middleware.sms.util.HttpUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 发送短信抽象接口--核心
 * 那么具体的短信平台适配就需要由子类实现了，
 * 那就肯定需要对应的配置类，因此该类中必须有一个配置类的字段，
 * 但由于各平台的配置类又不尽相同，因此就需要使用泛型啦！
 * 就定义在该抽象类中，继承统一的接口即可！
 * 这种玩法在多方适配的场景下常用，务必掌握！
 *
 * @author zoutongkun
 */
@Slf4j
public abstract class AbstractSmsClient<C extends SmsProviderConfig> implements SmsClient {
    /**
     * 短信服务提供商配置id
     * 意义不大
     */
    @Getter
    private final String configId;
    /**
     * 这就是配置类！
     * 是泛型，但都是SmsSupplierConfig的子类，
     * 因此就可以使用该接口中的方法！
     * 在子类继承该抽象类时，就会传入对应的配置类，
     * 这就实现了千人千面的效果！
     */
    private final C config;
    /**
     * 线程池，用于异步发送
     */
    protected final Executor executor;

    /**
     * 定时线程池
     * 不用使用Timer啦！
     */
    private static final ScheduledExecutorService scheduleThreadPool = ThreadUtil.createScheduledExecutor(5);

    /**
     * httpClient，用于执行发送请求！
     * 要注意的是，对于通用的框架，我们要尽最大可能避免过度依赖第三方依赖，
     * 因此这里并没有直接使用各大平台官方提供的开箱即用的sdk，而是使用更本质的做法，
     * 即直接按照官方的api的要求拼接url访问以发送短信！
     */
    protected final HttpUtil httpUtil = HttpUtil.getInstance();

    protected AbstractSmsClient(C config, Executor executor) {
        this.configId = StrUtil.isEmpty(config.getConfigId()) ? getProviderName() : config.getConfigId();
        this.config = config;
        this.executor = executor;
    }

    protected AbstractSmsClient(C config) {
        this.configId = StrUtil.isEmpty(config.getConfigId()) ? getProviderName() : config.getConfigId();
        this.config = config;
        this.executor = SmsExecutor.getExecutor();
    }

    /**
     * 获取配置类，各子类会获取到子类自己设置的配置类
     *
     * @return
     */
    protected C getConfig() {
        return config;
    }

    /**
     * <p>说明：发送固定消息模板短信
     * <p>此方法将使用配置文件中预设的短信模板进行短信发送
     * <p>该方法指定的模板变量只能存在一个（配置文件中）
     * <p>如使用的是腾讯的短信，参数字符串中可以同时存在多个参数，
     * 使用 & 分隔例如：您的验证码为{1}在{2}分钟内有效，可以传为  message="xxxx"+"&"+"5"
     * sendMessage
     *
     * @param phone 接收短信的手机号
     *              message 消息内容
     * @author :zoutongkun
     */

    @Override
    public abstract SmsResponse sendMessage(String phone, String message);

    /**
     * <p>说明：使用自定义模板发送短信
     * sendMessage
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @author :zoutongkun
     */

    @Override
    public abstract SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages);

    /**
     * <p>说明：群发固定模板短信
     * massTexting
     *
     * @author :zoutongkun
     */

    @Override
    public abstract SmsResponse massTexting(List<String> phones, String message);

    /**
     * <p>说明：使用自定义模板群发短信
     * massTexting
     *
     * @author :zoutongkun
     */

    @Override
    public abstract SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages);

    /**
     * <p>说明：异步短信发送，固定消息模板短信
     * sendMessageAsync
     *
     * @param phone    要发送的号码
     * @param message  发送内容
     * @param callBack 回调
     * @author :zoutongkun
     */
    @Override
    public final void sendMessageAsync(String phone, String message, CallBack callBack) {
        CompletableFuture<SmsResponse> smsResponseCompletableFuture = CompletableFuture.supplyAsync(() -> sendMessage(phone, message), executor);
        //接收一个回调
        smsResponseCompletableFuture.thenAcceptAsync(callBack::callBack);
    }

    /**
     * <p>说明：异步发送短信，不关注发送结果
     * sendMessageAsync
     *
     * @param phone   要发送的号码
     * @param message 发送内容
     * @author :zoutongkun
     */
    @Override
    public final void sendMessageAsync(String phone, String message) {
        executor.execute(() -> {
            sendMessage(phone, message);
        });
    }

    /**
     * <p>说明：异步短信发送，使用自定义模板发送短信
     * sendMessage
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @param callBack   回调
     * @author :zoutongkun
     */

    @Override
    public final void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages, CallBack callBack) {
        CompletableFuture<SmsResponse> smsResponseCompletableFuture = CompletableFuture.supplyAsync(() -> sendMessage(phone, templateId, messages), executor);
        smsResponseCompletableFuture.thenAcceptAsync(callBack::callBack);
    }

    /**
     * <p>说明：异步短信发送，使用自定义模板发送短信，不关注发送结果
     * sendMessageAsync
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @author :zoutongkun
     */
    @Override
    public final void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages) {
        executor.execute(() -> {
            sendMessage(phone, templateId, messages);
        });
    }

    /**
     * <p>说明：使用固定模板发送延时短信
     * delayedMessage
     *
     * @param phone       接收短信的手机号
     * @param message     要发送的短信
     * @param delayedTime 延迟时间
     * @author :zoutongkun
     */
    @Override
    public final void delayedMessage(String phone, String message, Long delayedTime) {
//        注意:只是延迟一下，而不是定时发送,因此使用schedule方法即可!
        scheduleThreadPool.schedule(() -> {
            try {
                sendMessage(phone, message);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new SmsException(e.getMessage());
            }
        }, delayedTime, TimeUnit.SECONDS);
    }

    /**
     * <p>说明：使用自定义模板发送定时短信 sendMessage
     * delayedMessage
     *
     * @param templateId  模板id
     * @param messages    key为模板变量名称 value为模板变量值
     * @param phone       要发送的手机号
     * @param delayedTime 延迟的时间
     * @author :zoutongkun
     */
    @Override
    public final void delayedMessage(String phone, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        scheduleThreadPool.schedule(() -> {
            try {
                sendMessage(phone, templateId, messages);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new SmsException(e.getMessage());
            }
        }, delayedTime, TimeUnit.SECONDS);

    }

    /**
     * <p>说明：群发延迟短信
     * delayMassTexting
     *
     * @param phones 要群体发送的手机号码
     * @author :zoutongkun
     */
    @Override
    public final void delayMassTexting(List<String> phones, String message, Long delayedTime) {
        scheduleThreadPool.schedule(() -> {
            try {
                massTexting(phones, message);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new SmsException(e.getMessage());
            }
        }, delayedTime, TimeUnit.SECONDS);
    }

    /**
     * <p>说明：使用自定义模板发送群体延迟短信
     * delayMassTexting
     *
     * @param phones      要群体发送的手机号码
     * @param templateId  模板id
     * @param messages    key为模板变量名称 value为模板变量值
     * @param delayedTime 延迟的时间
     * @author :zoutongkun
     */
    @Override
    public final void delayMassTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        scheduleThreadPool.schedule(() -> {
            try {
                massTexting(phones, templateId, messages);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new SmsException(e.getMessage());
            }
        }, delayedTime, TimeUnit.SECONDS);
    }
}
