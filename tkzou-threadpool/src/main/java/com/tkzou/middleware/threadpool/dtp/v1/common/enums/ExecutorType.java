package com.tkzou.middleware.threadpool.dtp.v1.common.enums;

import com.tkzou.middleware.threadpool.dtp.v2.MyDtpExecutor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author zoutongkun
 * @Date 2023/5/20 14:56
 */
@Getter
public enum ExecutorType {
    COMMON("common", MyDtpExecutor.class);
    private final String type;
    private final Class<? extends Executor> clazz;

    private static final Map<String, Class<? extends Executor>> TYPE_MAPPING = new HashMap<>();

    static {
        for (ExecutorType value : ExecutorType.values()) {
            TYPE_MAPPING.put(value.type, value.clazz);
        }
    }

    ExecutorType(String type, Class<? extends Executor> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public static Class<? extends Executor> getClazz(String type) {
        return TYPE_MAPPING.get(type);
    }
}
