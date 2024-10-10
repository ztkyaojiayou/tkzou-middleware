package com.tkzou.middleware.transaction.mqretry.v1.constant;

/**
 * @author :zoutongkun
 * @date :2023/10/3 12:46 上午
 * @description :
 * @modyified By:
 */
public class RetryConstant {
    /**
     * 需要重试的消息的标识，value就使用db中消息的id
     */
    public static final String RESEND_FLAG = "mq_message_resend_flag";
    /**
     * 记录消息发送成功的topic
     * 需要注意的是，这个消息可能是第一次消费的消息，也可能是重试的消息
     */
    public static final String MSG_SUCCESS_RECORD_TOPIC = "msg_success_record_topic";
    /**
     * 记录消息发送失败的topic
     * 同理，这个消息可能是第一次消费的消息，也可能是重试的消息
     */
    public static final String MSG_FAIL_RECORD_TOPIC = "msg_fail_record_topic";
}
