package com.tkzou.middleware.doublecache.core.notify;

/**
 * 缓存更新通知
 * 目的就是删除各节点的本地缓存，保证一致性！
 *
 * @author zoutongkun
 * @date 2024/4/15
 */
public interface NotifyService {

    /**
     * 发送缓存变更消息
     *
     * @param cacheNames
     */
    void sendMessage(String[] cacheNames);

    /**
     * 发送缓存变更消息
     *
     * @param cacheName
     */
    void sendMessage(String cacheName);

    /**
     * 发送缓存变更消息
     *
     * @param cacheName
     * @param key
     */
    void sendMessage(String cacheName, Object key);

    /**
     * 发送缓存批量变更消息
     *
     * @param cacheNames
     * @param key
     */
    void sendMessage(String[] cacheNames, Object key);
}
