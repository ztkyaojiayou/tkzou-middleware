package com.tkzou.middleware.threadpool.dtp.v2;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 拒绝策略增强类
 *
 * @author zoutongkun
 */
@Slf4j
public class RejectedInvocationHandler implements InvocationHandler {
    private Object target;
    private String threadPoolId;
    private final AtomicLong rejectCnt = new AtomicLong(0);
    /**
     * 最大拒绝次数
     */
    private Long maxRejectNum;

    /**
     * 构造器
     *
     * @param target       拒绝策略
     * @param threadPoolId 线程池id
     */
    public RejectedInvocationHandler(RejectedExecutionHandler target, String threadPoolId) {
        this.target = target;
        this.threadPoolId = threadPoolId;
    }

    public RejectedInvocationHandler(Object target, String threadPoolId, Long maxRejectNum) {
        this.target = target;
        this.threadPoolId = threadPoolId;
        this.maxRejectNum = maxRejectNum;
    }

    /**
     * 拒绝策略的增强逻辑
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCnt.incrementAndGet();
        log.warn("线程池：" + threadPoolId + "被拒绝次数：" + rejectCnt);
        if (rejectCnt.longValue() > maxRejectNum * 0.8) {
            //todo 可发邮件通知
            log.info("拒绝次数达到阈值，已发邮件通知。。。。。。。。。。。。。");
        }
        //执行原方法
        return method.invoke(target, args);
    }
}
