package com.tkzou.middleware.localmsgretry.mqretry.v2.processor;

import java.util.Map;

/**
 * 消息处理器接口
 *
 * @param <E>
 * @author zoutongkun
 */
public interface IBaseMessageProcessor<E extends Enum<E>> {
    String TYPE_KEY = "type";

    /**
     * 处理kafka消息
     *
     * @param key
     * @param type
     * @param messageObject
     */
    void processMessage(String key, E type, Map<Object, Object> messageObject);
}
