package com.tkzou.middleware.localmsgretry.methodretry.service;

import java.util.Objects;

/**
 * 方法执行上下文
 * 保存方法执行的状态
 * 开始执行时：设置为ture
 * 有值：正在执行
 * 无值：已经执行完成
 * <p>
 * Date: 2024-03-02
 *
 * @author zoutongkun
 */
public class MethodInvokeContextHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 当前方法是否正在执行
     *
     * @return
     */
    public static boolean isInvoking() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    /**
     * 当前方法正在执行
     */
    public static void setInvoking() {
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    /**
     * 当前方法执行完毕
     */
    public static void invoked() {
        INVOKE_THREAD_LOCAL.remove();
    }
}
