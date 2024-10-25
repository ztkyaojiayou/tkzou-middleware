package com.tkzou.middleware.threadpool.dtp.v1.core.core;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.threadpool.dtp.v1.common.constant.DtpConfigConstant;
import com.tkzou.middleware.threadpool.dtp.v1.common.support.ExecutorDecorator;
import com.tkzou.middleware.threadpool.dtp.v1.common.support.ExecutorWrapper;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


/**
 * 管理线程池
 *
 * @author zoutongkun
 * @Date 2023/5/19 22:08
 */
@Slf4j
public class DtpRegistry {
    /**
     * 储存线程池
     */
    private static final Map<String, ExecutorWrapper> EXECUTOR_MAP = new ConcurrentHashMap<>();

    public static void registry(String executorName, ExecutorDecorator<?> executorDecorator) {
        //包装
        ExecutorWrapper executorWrapper = wrap(executorName, executorDecorator);
        //注册
        EXECUTOR_MAP.put(executorName, executorWrapper);
        log.info("注册线程池成功：{}，核心线程数：{}，最大线程数：{}，任务队列：{}",
            executorName,
            executorDecorator.getCorePoolSize(),
            executorDecorator.getMaximumPoolSize(),
            executorDecorator.getQueue());
    }

    /**
     * 列出所有线程池
     *
     * @return 线程池集合
     */
    public static Set<String> listAll() {
        return EXECUTOR_MAP.keySet();
    }

    public static Executor getExecutor(String executorName) {
        ExecutorWrapper executorWrapper = EXECUTOR_MAP.get(executorName);
        if (executorWrapper == null) {
            throw new NullPointerException("不存在此线程池,{" + executorName + "}");
        }
        return executorWrapper.getExecutor();
    }

    public static ExecutorDecorator<?> getExecutorAdapter(String executorName) {
        ExecutorWrapper executorWrapper = EXECUTOR_MAP.get(executorName);
        if (executorWrapper == null) {
            throw new NullPointerException("不存在此线程池,{" + executorName + "}");
        }
        return executorWrapper.getExecutor();
    }

    /**
     * 刷新线程池参数
     *
     * @param executorName 线程池名字
     * @param properties   线程池参数
     */
    public static void refresh(String executorName, ThreadPoolProperties properties) {
        refresh(executorName, assembleParams(properties));
    }

    public static void refresh(String executorName, Map<String, Object> properties) {
        ExecutorWrapper executorWrapper = EXECUTOR_MAP.get(executorName);
        if (Objects.isNull(executorWrapper)) {
            log.info("刷新失败，不存在该线程池：{}", executorName);
            return;
        }
        ExecutorDecorator<?> executor = executorWrapper.getExecutor();
        //记录原数据
        int oldCorePoolSize = executor.getCorePoolSize();
        int oldMaximumPoolSize = executor.getMaximumPoolSize();
        int oldQueueSize = executor.getQueue().size();
        //先处理最大线程数
        Object maximumPoolSize = properties.get(DtpConfigConstant.MAXIMUM_POOL_SIZE);
        if (!Objects.isNull(maximumPoolSize)) {
            try {
                ReflectUtil.invoke(executor, StrUtil.genSetter(DtpConfigConstant.MAXIMUM_POOL_SIZE), maximumPoolSize);
                //设置好了就移除
                properties.remove(DtpConfigConstant.MAXIMUM_POOL_SIZE);
            } catch (UtilException e) {
                log.info("刷新失败，线程池参数有误! {}, 错误参数：{}， 值：{}",
                    executorName,
                    DtpConfigConstant.MAXIMUM_POOL_SIZE,
                    maximumPoolSize);
                return;
            }
        }
        //再处理剩余参数
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String property = entry.getKey();
            Object value = entry.getValue();
            try {
                ReflectUtil.invoke(executor, StrUtil.genSetter(property), value);
            } catch (UtilException e) {
                log.info("刷新失败，线程池参数有误! {}, 错误参数：{}， 值：{}", executorName, property, value);
            }
        }
        log.info("刷新线程池成功：{}，核心线程数：{} -> {}，最大线程数：{} -> {}",
            executorName,
            oldCorePoolSize,
            executor.getCorePoolSize(),
            oldMaximumPoolSize,
            executor.getMaximumPoolSize());
    }

    /**
     * 封装成包装类
     *
     * @param executorName      线程池名字
     * @param executorDecorator 实现了适配器的线程池
     * @return 线程池包装
     */
    private static ExecutorWrapper wrap(String executorName, ExecutorDecorator<?> executorDecorator) {
        return ExecutorWrapper.builder()
            .executorName(executorName)
            .executor(executorDecorator)
            .build();
    }

    private static Map<String, Object> assembleParams(ThreadPoolProperties threadPoolProperties) {
        Map<String, Object> res = new HashMap<>();
        res.put(DtpConfigConstant.MAXIMUM_POOL_SIZE, threadPoolProperties.getMaximumPoolSize());
        res.put(DtpConfigConstant.CORE_POOL_SIZE, threadPoolProperties.getCorePoolSize());
        return res;
    }
}
