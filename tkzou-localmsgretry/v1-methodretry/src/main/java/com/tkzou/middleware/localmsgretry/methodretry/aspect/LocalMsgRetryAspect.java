package com.tkzou.middleware.localmsgretry.methodretry.aspect;

import cn.hutool.core.date.DateUtil;
import com.tkzou.middleware.localmsgretry.methodretry.annotation.LocalMsgRetryable;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import com.tkzou.middleware.localmsgretry.methodretry.entity.RetryMethodMetadata;
import com.tkzou.middleware.localmsgretry.methodretry.service.MethodInvokeContextHolder;
import com.tkzou.middleware.localmsgretry.methodretry.service.MethodRetryService;
import com.tkzou.middleware.localmsgretry.methodretry.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 本地消息表方法重试切面
 * <p>
 * Date: 2024-04-20
 *
 * @author zoutongkun
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)//确保最先执行
@Component
public class LocalMsgRetryAspect {
    @Autowired
    private MethodRetryService methodRetryService;

    @Around("@annotation(localMsgRetryable)")
    public Object around(ProceedingJoinPoint joinPoint, LocalMsgRetryable localMsgRetryable) throws Throwable {
        boolean async = localMsgRetryable.isAsync();
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //非事务状态，直接执行，不做任何保证，也即失败了也不重试，当然，也是可以做的！！！
        if (MethodInvokeContextHolder.isInvoking() || !inTransaction) {
            return joinPoint.proceed();
        }

        //先组装成要入库的实体
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters =
                Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        RetryMethodMetadata curMethodMetadata = RetryMethodMetadata.builder()
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .build();
        MethodRetryRecord methodRetryRecord = MethodRetryRecord.builder()
                .retryMethodMetadataJson(curMethodMetadata)
                .maxRetryTimes(localMsgRetryable.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) MethodRetryService.RETRY_INTERVAL_MINUTES))
                .build();
        //执行方法
        methodRetryService.handle(methodRetryRecord, async);
        return null;
    }
}
