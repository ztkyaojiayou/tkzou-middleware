//package com.tkzou.middleware.localmsgretry.mqretry.v2.aspect;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.tkzou.middleware.localmsgretry.mqretry.v2.processor.IKafkaMessageProcessor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.annotation.Validated;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * kafka消息重试切面
// * 本质还是本地消息表方案！
// * 但这个思路挺好的！
// *
// * @author zoutongkun
// */
//@Aspect
//@Component
//@Slf4j
//public class MessageExceptionHandleAspect {
//    @Autowired
//    private MessageRetryProperties messageRetryProperties;
//    @Autowired
//    private KafkaMessageRetrievalModel messageRetrievalModel;
//    @Autowired
//    private ConsumerPoolService poolService;
//    @Autowired
//    private ConsumerGroupErrorLoader consumerGroupErrorLoader;
//    @Autowired
//    private ConsumerGroupContextFactory groupContextFactory;
//
//    /**
//     * 环绕通知
//     * 切processor包下所有类中的processRecord方法!
//     *
//     * @param jp
//     * @throws Throwable
//     */
//    @Around("execution(* com.tkzou.middleware.localmsgretry.mqretry.v2.processor.*
//    .processRecord(..))")
//    public void aroundProcess(ProceedingJoinPoint jp) throws Throwable {
//        Class<?> targetClass = jp.getTarget().getClass();
//        Object[] args = jp.getArgs();
//        if (ArrayUtils.isEmpty(args)) {
//            throw new RuntimeException("No args found.");
//        }
//
//        Object arg = args[0];
//        if (!(arg instanceof ConsumerRecord)) {
//            throw new RuntimeException("arg is not ConsumerRecord");
//        }
//
//        ConsumerRecord<String, String> record = (ConsumerRecord<String, String>) arg;
//        String key = record.key();
//        String message = record.value();
//        Map<Object, Object> messageMap = JsonUtils.from(message, new TypeReference<Map<Object,
//        Object>>() {
//        });
//        String type = messageMap.get(TYPE_KEY).toString();
//        if (StringUtils.isBlank(type)) {
//            throw new RuntimeException("Type is blank, message: " + message);
//        }
//
//        if (!IKafkaMessageProcessor.class.isAssignableFrom(targetClass)) {
//            throw TropicoException.error("Target class is not assignable {}", targetClass
//            .getSimpleName());
//        }
//        Class<? extends IKafkaMessageProcessor> processor = (Class<? extends
//        IKafkaMessageProcessor>) targetClass;
//        ConsumerGroup consumerGroup = groupContextFactory.getConsumerGroupFromProcessor
//        (processor);
//        KafkaTopic topic = groupContextFactory.getTopicFromProcessor(processor);
//        //使用重试规则检查
//        boolean preCheckResult = preCheck(consumerGroup, key, type, messageMap);
//        if (!preCheckResult) {
//            //入库
//            messageRetrievalModel.insert(topic, consumerGroup.name(), key, type, JsonUtils
//            .toString(messageMap),
//                    "Blocked by preCheck");
//            log.warn("Blocking in process message. Message is blocked by handler.");
//            return;
//        }
//        try {
//            //执行目标方法
//            jp.proceed();
//        } catch (Exception e) {
//            //失败时也入库
//            messageRetrievalModel.insert(topic, consumerGroup.name(), key, type, JsonUtils
//            .toString(messageMap),
//                    StringUtils.left(e.getMessage(), 65535));
//            pauseByFailure(consumerGroup);
//            log.warn("Error in process message. Exception is record by handler.", e);
//        }
//
//    }
//
//    private boolean preCheck(ConsumerGroup consumerGroup, Object key, String type, Map
//    messageMap) {
//
//
//        Map<ConsumerGroup, MessageRetryProperties.Regulation> regulationMap =
//        messageRetryProperties.getRegulation();
//        MessageRetryProperties.Regulation regulation = regulationMap.get(consumerGroup);
//        if (regulation == null) {
//            return true;
//        }
//        MessageRetryProperties.BlockingRegulation blockOnException = regulation
//        .getBlockOnException();
//        if (blockOnException != null && blockOnException.getMode() != null && CollectionUtils
//        .isNotEmpty(blockOnException.getType())) {
//            switch (blockOnException.getMode()) {
//                case USER_ALL_TYPE:
//                    List<Long> messageIdByUserList = messageRetrievalModel.fetchByPartitionKey
//                    (key.toString(),
//                            blockOnException.getThreshold());
//                    if (messageIdByUserList.size() > blockOnException.getThreshold()) {
//                        return false;
//                    }
//                    break;
//                case USER_THIS_TYPE:
//                    List<Long> messageIdByUserAndTypeList =
//                            messageRetrievalModel.fetchByTypeAndPartitionKey(consumerGroup.name
//                            (), type,
//                                    key.toString(), blockOnException.getThreshold());
//                    if (messageIdByUserAndTypeList.size() > blockOnException.getThreshold()) {
//                        return false;
//                    }
//                    break;
//                default:
//                    throw new RuntimeException("Mode not found");
//            }
//        }
//        return true;
//    }
//}
