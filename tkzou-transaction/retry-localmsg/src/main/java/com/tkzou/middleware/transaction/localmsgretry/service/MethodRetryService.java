package com.tkzou.middleware.transaction.localmsgretry.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.tkzou.middleware.transaction.localmsgretry.entity.MethodRetryRecord;
import com.tkzou.middleware.transaction.localmsgretry.entity.RetryMethodMetadata;
import com.tkzou.middleware.transaction.localmsgretry.mapper.LocalMsgRetryRecordDao;
import com.tkzou.middleware.transaction.localmsgretry.util.JsonUtils;
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
 * 方法重试服务
 * 参考：https://mp.weixin.qq.com/s?__biz=MzU3MDAzNDg1MA==&mid=2247533040&idx=1&sn
 * =cb5ea58167cefe8e73cf5a260b0b65cd&chksm
 * =fdc2f120ad5fa53c29ae5affb55271454b6ac5d1541b84ab682ad5dac62d73ddc29ff2e6c0f5&scene=90&xtrack
 * =1&sessionid=1713405355&subscene=93&clicktime=1713405360&enterid=1713405360&flutter_pos=0
 * &biz_enter_id=4&ascene=56&devicetype=iOS15.4
 * .1&version=18002a32&nettype=3G+&abtest_cookie=AAACAA%3D%3D&lang=zh_CN&countrycode=CN&fontScale
 * =106&exportkey=n_ChQIAhIQfo5Lxa17c1QvJgm9BnzMnxLXAQIE97dBBAEAAAAAAEszBw7
 * %2BlRAAAAAOpnltbLcz9gKNyK89dVj0rpIQvZdtB0%2F9oTcyo%2BhU6s7xHMbXftiR
 * %2BMZ9Viuuob9ajBr4kAXZvGO1VrZowIcnP
 * %2B7rxmgVGjw1HmLMiEhcjhAcb35wZIavo9SyJ2cV1yJTJXcoOU4RXr7D2eznne8yzu1e3tybKfXRPSoNqW9DpNCKyu0fz0EkDnDJs%2B7bVNvfFLTbKy8bVaEHngIg1kvWiUg9hJO%2B608uzjWPud1dd%2FxvRoGuAvhYkSFttCF%2Fr%2Bt6&pass_ticket=%2BdhHrOGvGgVSp1p5exANhRqBqH3MvBFIcraWLaHSbqyBUVZCgfl%2Bao0oyZbmhkIhlKHePnKwylzHnmMxQC2N%2Bw%3D%3D&wx_header=3
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
        // 判断当前是否存在事务
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //1.非事务状态，前面已经执行。
        if (!inTransaction) {
            return;
        }

        //保存执行数据--注意：此时和前面的操作是在同一个事务中的，这一点非常重要！！！
        saveRecord(record);
        //2.则添加一个事务同步器，并重写afterCompletion方法（此方法在事务提交后会做回调）
        //作用是当事务提交后（此时这条执行rpc方法的消息就确保在本地事务写成功啦！）执行一次该rpc方法，
        // 此时即使超时了也无所谓（若能返回确定的成功或失败那是最好！），重试即可！
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            /**
             * 事务提交后会做的事情，该方法无法获取到当前事务的状态
             */
            @SneakyThrows
            @Override
            public void afterCommit() {
                //事务提交完后回调该方法来执行，也即重试逻辑不能影响主事务，它不应该是事务中的一部分，
                //因此需要在原事务提交之后再执行，防止因为该重试逻辑执行失败影响主事务的提交，这是不合适的！
                //这也叫支持事务，即假设我们的重试方法是在事务方法内部调用的，那么我们需要保证事务提交后再执行这个重试方法。
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }

            /**
             * 事务完成后要做的事情，此时事务肯定已经提交了，
             * 此时可以获取到当前事务的状态，这是二者的主要区别，更推荐重写该方法
             * 此时我们可以根据这个状态来做不同的事情，
             * 比如：可以在事务提交时做自定义处理，也可以在事务回滚时做自定义处理等，非常灵活！！！
             *
             * @param status
             */
            @Override
            public void afterCompletion(int status) {
                //显示判断事务是否已经提交，更保险！
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    //此时就可以执行我们的业务逻辑了
                    //todo
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

    /**
     * 通过反射机制执行指定方法
     * 第一次或重试都是执行该方法
     * 注意：当第二次执行时（即第二个线程！）此时又会走到切面逻辑，此时就不会开事务啦，
     * 因为该方法本身没有加事务注解，但是TransactionSynchronizationManager.isActualTransactionActive()
     * 还是返回true，可能是bug，
     * 因此我们使用一个threadLocal来维护一下即可！
     *
     * @param record
     */
    public void doInvoke(MethodRetryRecord record) {
        RetryMethodMetadata retryMethodMetadata = record.getRetryMethodMetadataJson();
        try {
            //维护一下MethodInvokeContextHolder
            MethodInvokeContextHolder.setInvoking();
            //解析要重试的方法的元信息，通过反射机制执行方法
            Class<?> beanClass = Class.forName(retryMethodMetadata.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings =
                JsonUtils.toList(retryMethodMetadata.getParameterTypes(), String.class);
            List<Class<?>> parameterClasses = getMethodParameters(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass, retryMethodMetadata.getMethodName(),
                parameterClasses.toArray(new Class[]{}));
            Object[] args = getMethodArgs(retryMethodMetadata, parameterClasses);
            //执行方法（第一次或重试），因为一般是写数据，因此无需考虑返回值！
            method.invoke(bean, args);
            //执行成功删除该记录
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            //执行失败或超时！！！此时不回滚前面的操作，而是更新本地消息表，通过job重试来保证其成功即可！！！
            updateRecord(record, e.getMessage());
        } finally {
            //再清理一下
            MethodInvokeContextHolder.invoked();
        }
    }

    @NotNull
    private Object[] getMethodArgs(RetryMethodMetadata retryMethodMetadata,
                                   List<Class<?>> parameterClasses) {
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

    /**
     * 新增本地消息
     *
     * @param record
     */
    private void saveRecord(MethodRetryRecord record) {
        localMsgRetryRecordDao.save(record);
    }

    /**
     * 更新本地消息
     *
     * @param record
     * @param errorMsg
     */
    private void updateRecord(MethodRetryRecord record, String errorMsg) {
        MethodRetryRecord update = new MethodRetryRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        //重试次数大于注解上配置的3次后就直接视为失败！
        //此时重试次数就不用管了，统一置为-1即可
        //更新重试次数
        Integer retryTimes = record.getRetryTimes() + 1;
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setRetryTimes(-1);
            update.setStatus(MethodRetryRecord.STATUS_FAIL);
        } else {
            update.setNextRetryTime(genNextRetryTime(retryTimes));
            update.setRetryTimes(retryTimes);
        }
        localMsgRetryRecordDao.updateById(update);
    }

    /**
     * 删除本地消息
     *
     * @param id
     */
    private void removeRecord(Long id) {
        localMsgRetryRecordDao.removeById(id);
    }

    /**
     * 构建下次重试时间
     *
     * @param retryTimes
     * @return
     */
    private Date genNextRetryTime(Integer retryTimes) {//或者可以采用退避算法
        //指数运算
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);//重试时间指数上升 即：2m 4m 8m 16m
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

}
