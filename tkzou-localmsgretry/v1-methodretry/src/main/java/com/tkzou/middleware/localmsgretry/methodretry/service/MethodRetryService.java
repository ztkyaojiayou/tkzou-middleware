package com.tkzou.middleware.localmsgretry.methodretry.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import com.tkzou.middleware.localmsgretry.methodretry.entity.RetryMethodMetadata;
import com.tkzou.middleware.localmsgretry.methodretry.mapper.LocalMsgRetryRecordDao;
import com.tkzou.middleware.localmsgretry.methodretry.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Description: 安全执行处理器
 * <p>
 * Date: 2024-03-20
 *
 * @author zoutongkun
 */
@Slf4j
@AllArgsConstructor
public class MethodRetryService {

    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final LocalMsgRetryRecordDao localMsgRetryRecordDao;

    private final Executor executor;

    /**
     * 处理要执行的方法
     *
     * @param record
     * @param async
     */
    public void handle(MethodRetryRecord record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //非事务状态，前面已经执行。
        if (!inTransaction) {
            return;
        }
        //保存执行数据
        saveRecord(record);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                //事务后执行
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }
        });
    }

    public void doAsyncInvoke(MethodRetryRecord record) {
        executor.execute(() -> {
            System.out.println(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    public void doInvoke(MethodRetryRecord record) {
        RetryMethodMetadata retryMethodMetadata = record.getRetryMethodMetadataJson();
        try {
            //维护一下MethodInvokeContextHolder
            MethodInvokeContextHolder.setInvoking();
            //解析要重试的方法的元信息，通过反射机制执行方法
            Class<?> beanClass = Class.forName(retryMethodMetadata.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JsonUtils.toList(retryMethodMetadata.getParameterTypes(), String.class);
            List<Class<?>> parameterClasses = getMethodParameters(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass, retryMethodMetadata.getMethodName(),
                    parameterClasses.toArray(new Class[]{}));
            Object[] args = getMethodArgs(retryMethodMetadata, parameterClasses);
            //执行方法
            method.invoke(bean, args);
            //执行成功删除该记录
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            //执行失败，等待下次执行
            updateRecord(record, e.getMessage());
        } finally {
            MethodInvokeContextHolder.invoked();
        }
    }

    @NotNull
    private Object[] getMethodArgs(RetryMethodMetadata retryMethodMetadata, List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(retryMethodMetadata.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }

    @NotNull
    private List<Class<?>> getMethodParameters(List<String> parameterStrings) {
        return parameterStrings.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("SecureInvokeService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }

    private void saveRecord(MethodRetryRecord record) {
        localMsgRetryRecordDao.save(record);
    }

    private void updateRecord(MethodRetryRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        MethodRetryRecord update = new MethodRetryRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setStatus(MethodRetryRecord.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        localMsgRetryRecordDao.updateById(update);
    }

    private void removeRecord(Long id) {
        localMsgRetryRecordDao.removeById(id);
    }

    private Date getNextRetryTime(Integer retryTimes) {//或者可以采用退避算法
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);//重试时间指数上升 2m 4m 8m 16m
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

}
