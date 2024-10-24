package com.tkzou.middleware.transaction.mqretry.v3.retry;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 消费侧重试抽象类
 * 要注意的是，消费侧是不能抛异常的，否则会阻塞消息的消费，我们必须全部catch起来！
 * 参考：https://mp.weixin.qq.com/s/0P3x2ms17sPrC1EWSAAGYw
 *
 * @author zoutongkun
 */
public abstract class BaseMessageRetryService {

    private static final Logger log = LoggerFactory.getLogger(BaseMessageRetryService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 初始化消息
     *
     * @param message
     */
    public void initMessage(Message message) {
        log.info("{} 收到消息: {}，业务数据：{}", this.getClass().getName(), message.toString(), new String(message.getBody()));
        try {
            //封装消息
            MessageRetryDTO messageRetryDto = buildMessageRetryInfo(message);
            if (log.isInfoEnabled()) {
                log.info("反序列化消息:{}", messageRetryDto.toString());
            }
            //准备执行业务方法
            prepareAction(messageRetryDto);
        } catch (Exception e) {
            log.warn("处理消息异常，错误信息：", e);
        }
    }

    /**
     * 准备执行
     *
     * @param retryDto
     */
    protected void prepareAction(MessageRetryDTO retryDto) {
        try {
            //1.执行业务方法
            execute(retryDto);
            //2.成功时的回调
            doSuccessCallBack(retryDto);
        } catch (Exception e) {
            log.error("当前任务执行异常，业务数据：" + retryDto.toString(), e);
            //3.执行失败时，计算是否还需要继续重试
            //要注意的是，这里的重试此时就是统计mq消费的次数，而不是咱们手动执行该消息的方法来重试！！！
            //3.1继续重试，也即继续投递到mq！
            //todo 更推荐直接入库，然后使用job统一投递，投递成功后就删除，这种方式设置都不需要考虑投递次数，参考v2中yqg的做法！
            if (retryDto.checkRetryCount()) {
                if (log.isInfoEnabled()) {
                    log.info("重试消息:{}", retryDto);
                }
                //把当前消费失败的消息重新投递到当前topic中进行重试！
                retrySend(retryDto);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("当前任务重试次数已经到达最大次数，业务数据：" + retryDto, e);
                }
                //3.2若重试此时用完后还未成功，此时就入库，人工介入
                doFailCallBack(retryDto.setErrorMsg(e.getMessage()));
            }
        }
    }

    /**
     * 任务执行成功，回调服务(根据需要进行重写)
     *
     * @param messageRetryDto
     */
    private void doSuccessCallBack(MessageRetryDTO messageRetryDto) {
        try {
            successCallback(messageRetryDto);
        } catch (Exception e) {
            log.warn("执行成功回调异常，队列描述：{}，错误原因：{}", messageRetryDto.getSourceDesc(), e.getMessage());
        }
    }

    /**
     * 任务执行失败，回调服务(根据需要进行重写)
     *
     * @param messageRetryDto
     */
    private void doFailCallBack(MessageRetryDTO messageRetryDto) {
        try {
            saveMessageRetryInfo(messageRetryDto.setErrorMsg(messageRetryDto.getErrorMsg()));
            failCallback(messageRetryDto);
        } catch (Exception e) {
            log.warn("执行失败回调异常，队列描述：{}，错误原因：{}", messageRetryDto.getSourceDesc(), e.getMessage());
        }
    }

    /**
     * 执行任务
     *
     * @param messageRetryDto
     */
    protected abstract void execute(MessageRetryDTO messageRetryDto);

    /**
     * 成功回调
     *
     * @param messageRetryDto
     */
    protected abstract void successCallback(MessageRetryDTO messageRetryDto);

    /**
     * 失败回调
     *
     * @param messageRetryDto
     */
    protected abstract void failCallback(MessageRetryDTO messageRetryDto);

    /**
     * 构建消息补偿实体
     *
     * @param message
     * @return
     */
    private MessageRetryDTO buildMessageRetryInfo(Message message) {
        //如果头部包含补偿消息实体，直接返回
        Map<String, Object> messageHeaders = message.getMessageProperties().getHeaders();
        //使用一个特定的请求头传递重试信息
        if (messageHeaders.containsKey("message_retry_info")) {
            Object retryMsg = messageHeaders.get("message_retry_info");
            if (Objects.nonNull(retryMsg)) {
                return JSONObject.parseObject(String.valueOf(retryMsg), MessageRetryDTO.class);
            }
        }
        //自动将业务消息加入补偿实体
        MessageRetryDTO messageRetryDto = new MessageRetryDTO();
        messageRetryDto.setBodyMsg(new String(message.getBody(), StandardCharsets.UTF_8));
        messageRetryDto.setExchangeName(message.getMessageProperties().getReceivedExchange());
        messageRetryDto.setRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
        messageRetryDto.setQueueName(message.getMessageProperties().getConsumerQueue());
        messageRetryDto.setCreateTime(new Date());
        return messageRetryDto;
    }

    /**
     * 异常消息重新发送到mq
     * 且topic也不变，就是重新投递
     *
     * @param retryDto
     */
    private void retrySend(MessageRetryDTO retryDto) {
        //将补偿消息实体放入头部，原始消息内容保持不变
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setHeader("message_retry_info", JSONObject.toJSON(retryDto));
        Message message = new Message(retryDto.getBodyMsg().getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(retryDto.getExchangeName(), retryDto.getRoutingKey(), message);
    }


    /**
     * 将异常消息存储到mongodb中
     *
     * @param retryDto
     */
    private void saveMessageRetryInfo(MessageRetryDTO retryDto) {
        try {
            mongoTemplate.save(retryDto, "message_retry_info");
        } catch (Exception e) {
            log.error("将异常消息存储到mongodb失败，消息数据：" + retryDto.toString(), e);
        }
    }
}
