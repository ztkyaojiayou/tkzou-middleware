//package com.tkzou.middleware.localmsgretry.mqretry.v2.job;
//
//import com.google.common.collect.Lists;
//import lombok.Data;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.validation.annotation.Validated;
//
//import java.util.List;
//
///**
// * @author zoutongkun
// */
//@Service
//@Slf4j
//public class KafkaMessageRetryJob  {
//
//  @Autowired
//  private KafkaMessageRetrievalModel retrievalModel;
//
//  @Autowired
//  private IKafkaMessageService kafkaMessageService;
//
//  @Autowired
//  private ThreadTransactionalModel threadTransactionalModel;
//
//  private final static Integer BATCH_SIZE = 500;
//
//  public void exec(JobExecutionContext context) throws Exception {
//    Param param = getParam(context, Param.class);
//    switch (param.getRetryBy()) {
//      case ID:
//        retryById(param.id);
//        break;
//      case USER:
//        retryByUser(param.getUserId());
//        break;
//      case TYPE:
//        retryByType(param.type);
//        break;
//      case ALL:
//        retryAll();
//        break;
//      default:
//        throw new RuntimeException("Error in RetryBy");
//    }
//  }
//
//  private void retryById(Long id) {
//    publishMessage(Lists.newArrayList(id));
//  }
//
//  private void retryByUser(Long userId) {
//    List<Long> retrievalIds = null;
//    do {
//      if (isInterrupted()) {
//        return;
//      }
//      Long minId = CollectionUtils.isEmpty(retrievalIds) ? 0L : retrievalIds.get(retrievalIds
//      .size() - 1);
//      retrievalIds = retrievalModel.fetchByPartitionKey(userId.toString(), minId, BATCH_SIZE);
//      publishMessage(retrievalIds);
//    }
//    while (CollectionUtils.isNotEmpty(retrievalIds));
//  }
//
//  private void retryByType(List<String> typeList) {
//    List<Long> retrievalIds = null;
//    do {
//      if (isInterrupted()) {
//        return;
//      }
//      Long minId = CollectionUtils.isEmpty(retrievalIds) ? 0L : retrievalIds.get(retrievalIds
//      .size() - 1);
//      retrievalIds = retrievalModel.fetchByType(typeList, minId, BATCH_SIZE);
//      publishMessage(retrievalIds);
//    } while (CollectionUtils.isNotEmpty(retrievalIds));
//  }
//
//  private void retryAll() {
//    List<Long> retrievalIds = null;
//    do {
//      if (isInterrupted()) {
//        return;
//      }
//      Long minId = CollectionUtils.isEmpty(retrievalIds) ? 0L : retrievalIds.get(retrievalIds
//      .size() - 1);
//      retrievalIds = retrievalModel.fetchIds(minId, BATCH_SIZE);
//      publishMessage(retrievalIds);
//    } while (CollectionUtils.isNotEmpty(retrievalIds));
//  }
//
//  private void publishMessage(List<Long> retrievalIds) {
//    for (Long retrievalId : retrievalIds) {
//      //加事务
//      threadTransactionalModel.transaction(configuration -> {
//        if (isInterrupted()) {
//          return;
//        }
//        //捞出需要重试的消息
//        KafkaMessageRetrievalRecord record = retrievalModel.fetchByIdForUpdate(retrievalId);
//        //重试，即重新投递
//        kafkaMessageService.schedule(record.getTopic(), record.getPartitionKey(), record
//        .getMessage());
//        //投递成功后直接删除该信息
//        retrievalModel.deleteRecord(record);
//      });
//    }
//  }
//
//  @Data
//  @Validated
//  public static class Param {
//    @NonNull
//    private RetryBy retryBy;
//    private Long id;
//    private Long userId;
//    private List<String> type;
//  }
//
//  public enum RetryBy {
//    ID,
//    USER,
//    TYPE,
//    ALL
//  }
//}
//
