package com.tkzou.middleware.transaction.mqretry.v1.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.tkzou.middleware.transaction.mqretry.v1.config.GlobalParamConfig;
import com.tkzou.middleware.transaction.mqretry.v1.constant.RetryConstant;
import com.tkzou.middleware.transaction.mqretry.v1.entity.FailMsgLog;
import com.tkzou.middleware.transaction.mqretry.v1.mapper.FailMsgLogMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;
import java.util.List;

/**
 * 失败消息重试job，实际的项目中会使用专门的xxl-job等，
 * 可以防止同一个job在多个节点上重复执行的问题！
 * 注意：重试的意思是重新将消费端处理失败的消息投递到kafka，
 * 而不是说直接在消费端再从数据库中将这个消息重新处理一次！！！
 * 是要再走一遍kafka，然后再消费一次，也即它并不是简单地执行一下消费端的某个方法，
 * 我们只需要把该消息重新给消费者消费一次即可！！！
 * 易知虽然本质都是重试，但这里是重新消费该消息，而一般的重试是重新执行一遍目标方法！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/10/03 00:41
 */
@Component
@Slf4j
public class FailMsgRetryJob {

    @Autowired
    FailMsgLogMapper failMsgLogMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private GlobalParamConfig paramConfig;

    /**
     * 重新发送/投递/重试失败消息
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void retryFailMsg() {
        List<String> resList = Lists.newArrayList();
        // 查询所有处理失败的消息
        List<FailMsgLog> failMsgLogs = getMqMsgFailLogs();

        for (FailMsgLog curFailMsg : failMsgLogs) {
            resList.add(curFailMsg.getId() + "," + curFailMsg.getTopic() + "," + curFailMsg.getMqKey());

            Integer resendTimes = curFailMsg.getResendTimes();
            String valueJson = curFailMsg.getMqValue();
            JSONObject valueObj = JSONObject.fromObject(valueJson);
            // 添加重发标志，value就使用id，用于标识当前消息为重试的消息！
            //后面会根据该标志对消息进行判断！
            valueObj.put(RetryConstant.RESEND_FLAG, curFailMsg.getId());
            valueJson = valueObj.toString();
            String key = StringUtils.isNotEmpty(curFailMsg.getMqKey()) ? curFailMsg.getMqKey() :
                null;
            // 重新向该消息对应的topic中发送/投递该消息，同时添加一个回调
            kafkaTemplate.send(curFailMsg.getTopic(), key, valueJson)
                .addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    /**
                     * 发送失败时，跳过，继续发即可！
                     * 这种不算重试，因为重试指的是消费者消费失败，生产者发送失败不算！！！
                     * @param ex
                     */
                    @Override
                    public void onFailure(Throwable ex) {
                        // do nothing
                    }

                    /**
                     * 发送成功时，更新db，重发次数加1，但是否消息成功的字段也即mqStatus则不在这里维护，
                     * 因为这里只能保证发送成功，而是否消费成功这里无法保证
                     * @param result
                     */
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        // 发送成功,更新db，重发次数加1
                        FailMsgLog log = new FailMsgLog();
                        log.setId(curFailMsg.getId());
                        Integer times = (resendTimes + 1);
                        log.setResendTimes(times);
                        log.setOpTime(new Date());
                        failMsgLogMapper.updateById(log);
                    }
                });
        }
        log.info("rehandleFailMq:{}", resList);
    }


    /**
     * 获取所有需要重试/重新投递的消息
     *
     * @return
     */
    private List<FailMsgLog> getMqMsgFailLogs() {
        LambdaQueryWrapper<FailMsgLog> qw = new LambdaQueryWrapper<>();
        //未完成处理的
        qw.eq(FailMsgLog::getMqStatus, 1);
        //重试次数小于6次的，也即这里最多只重试6次
        qw.le(FailMsgLog::getResendTimes, paramConfig.getRetryTime());
        return failMsgLogMapper.selectList(qw);
    }
}
