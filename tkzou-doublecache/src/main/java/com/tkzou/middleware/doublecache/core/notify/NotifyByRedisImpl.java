package com.tkzou.middleware.doublecache.core.notify;

import com.tkzou.middleware.doublecache.config.DoubleCacheConfig;
import com.tkzou.middleware.doublecache.core.listener.CacheUpdateMessage;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis发送服务实现接口
 *
 * @author zoutongkun
 * @date 2024/4/15
 */
public class NotifyByRedisImpl implements NotifyService {

    private static final Logger logger = LoggerFactory.getLogger(NotifyByRedisImpl.class);

    /**
     * 缓存配置属性
     */
    private DoubleCacheConfig doubleCacheConfig;

    /**
     * Redis操作接口
     */
    private RedissonClient redissonClient;

    public NotifyByRedisImpl(DoubleCacheConfig doubleCacheConfig,
                             RedissonClient redissonClient) {
        this.doubleCacheConfig = doubleCacheConfig;
        this.redissonClient = redissonClient;
    }

    /**
     * 发送缓存变更消息
     *
     * @param cacheNames
     */
    @Override
    public void sendMessage(String[] cacheNames) {
        sendMessage(cacheNames, null);
    }

    /**
     * 发送缓存变更消息
     *
     * @param cacheName
     */
    @Override
    public void sendMessage(String cacheName) {
        sendMessage(new String[]{cacheName}, null);
    }

    /**
     * 发送缓存变更消息
     *
     * @param cacheName
     * @param key
     */
    @Override
    public void sendMessage(String cacheName, Object key) {
        sendMessage(new String[]{cacheName}, key);
    }

    /**
     * 发送缓存变更消息
     *
     * @param cacheNames
     * @param key
     */
    @Override
    public void sendMessage(String[] cacheNames, Object key) {
        RTopic rTopic = redissonClient.getTopic(doubleCacheConfig.getTopic());
        long receive = rTopic.publish(new CacheUpdateMessage(cacheNames, key));
        logger.info("sendMessage receive clients: " + receive);
    }

}
