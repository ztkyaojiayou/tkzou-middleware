package com.tkzou.middleware.doublecache.core.cache;

import cn.hutool.core.util.ObjectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tkzou.middleware.doublecache.config.DoubleCacheConfig;
import com.tkzou.middleware.doublecache.core.notify.NotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 两级缓存实现
 * 也即一级/本地缓存+二级缓存实现，
 * 其中一级缓存这里使用的是Caffeine
 * 二级缓存就使用的redis
 *
 * @author zoutongkun
 * @date 2024/4/15
 */
public class SecondLevelCacheService implements DoubleCacheService {

    private static final Logger logger = LoggerFactory.getLogger(SecondLevelCacheService.class);

    /**
     * 二级缓存需要依赖redis缓存
     */
    private final DoubleCacheService firstCacheService;

    /**
     * Caffeine内部缓存
     * key：cacheName，理解为命名空间即可
     * value：cache，即具体的缓存
     */
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * Redis 发送服务接口
     * 用于保持缓存的一致性
     */
    private final NotifyService notifyService;

    /**
     * 缓存配置参数
     */
    private final DoubleCacheConfig doubleCacheConfig;


    public SecondLevelCacheService(DoubleCacheService firstCacheService,
                                   NotifyService notifyService,
                                   DoubleCacheConfig doubleCacheConfig) {
        this.firstCacheService = firstCacheService;
        this.notifyService = notifyService;
        this.doubleCacheConfig = doubleCacheConfig;
    }

    /**
     * 清理缓存（支持批量清理）
     *
     * @param cacheNames
     * @param key
     */
    public void clearNotSend(String[] cacheNames, Object key) {
        for (String cacheName : cacheNames) {
            //直接清理即可，无需通知其他节点了，因为当前消息就是由其他节点触发的！！！
            doClearAndSend(cacheName, key, false);
        }
    }

    /**
     * 初始化caffeine缓存对象
     *
     * @return
     */
    public Cache<Object, Object> caffeineCache() {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
        // Caffeine 缓存初始化参数配置
        if (doubleCacheConfig.getExpireAfterAccess() > 0) {
            cacheBuilder.expireAfterAccess(doubleCacheConfig.getExpireAfterAccess(), TimeUnit.MILLISECONDS);
        }
        if (doubleCacheConfig.getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(doubleCacheConfig.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        if (doubleCacheConfig.getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(doubleCacheConfig.getInitialCapacity());
        }
        if (doubleCacheConfig.getMaximumSize() > 0) {
            cacheBuilder.maximumSize(doubleCacheConfig.getMaximumSize());
        }
        if (doubleCacheConfig.getRefreshAfterWrite() > 0) {
            cacheBuilder.refreshAfterWrite(doubleCacheConfig.getRefreshAfterWrite(), TimeUnit.MILLISECONDS);
        }
        return cacheBuilder.build();
    }

    /**
     * 获取缓存对象
     *
     * @param cacheName
     * @param cacheKey
     * @return
     */
    @Override
    public Object get(final String cacheName, final Object cacheKey) {
        Object result = null;
        Cache caffeineCache = cacheMap.get(cacheName);
        if (null != caffeineCache) {
            // 1.先从本地缓存获取
            result = caffeineCache.getIfPresent(cacheKey);
        }

        if (null == result) {
            // 2.从Redis缓存获取
            result = firstCacheService.get(cacheName, cacheKey);
            logger.info("getFromCache # fetch data from redis cache.");
            if (ObjectUtil.isNotEmpty(result)) {
                // 3.再更新本地缓存，但无需发通知删除其他节点的本地缓存，因为此时redis的值并没有更新，只是单纯是本地缓存失效了！
                saveCaffeineCache(cacheName, cacheKey, result, caffeineCache);
            }
        }

        return result;
    }

    /**
     * 保存更新Caffeine缓存
     *
     * @param cacheName
     * @param cacheKey
     * @param result
     * @param caffeineCache
     */
    private void saveCaffeineCache(String cacheName, Object cacheKey, Object result, Cache caffeineCache) {
        if (null != result) {
            // 获取缓存对象
            if (caffeineCache == null) {
                caffeineCache = caffeineCache();
                cacheMap.putIfAbsent(cacheName, caffeineCache);
            }
            caffeineCache.put(cacheKey, result);
        }
    }

    @Override
    public boolean save(String[] cacheNames, Object cacheKey, Object cacheValue, long ttl) {
        //1.先写到redis
        boolean result = firstCacheService.save(cacheNames, cacheKey, cacheValue, ttl);
        //2.再保存本地缓存并广播其他节点以清除（而非保存！）二级缓存
        saveAndSend(cacheNames, cacheKey, cacheValue);
        return result;
    }

    @Override
    public boolean saveByAsync(String[] cacheNames, Object cacheKey, Object cacheValue, long ttl) {
        //1.先异步写到redis
        boolean result = firstCacheService.saveByAsync(cacheNames, cacheKey, cacheValue, ttl);
        //2.再保存本地缓存并广播其他节点以清除（而非保存！）二级缓存
        saveAndSend(cacheNames, cacheKey, cacheValue);
        return result;
    }

    @Override
    public boolean deleteByAsync(String[] cacheNames, Object cacheKey) {
        throw new RuntimeException("一级缓存目前不支持异步处理！");
    }

    @Override
    public boolean deleteByAsync(String[] cacheNames) {
        throw new RuntimeException("一级缓存目前不支持异步处理！");
    }

    @Override
    public boolean delete(String[] cacheNames, Object cacheKey) {
        //1.先删除redis
        boolean result = firstCacheService.delete(cacheNames, cacheKey);
        //2.再删除各个节点的本地缓存
        clearAndSend(cacheNames, cacheKey);
        return result;
    }

    @Override
    public boolean delete(String[] cacheNames) {
        //1.先删除redis
        boolean result = firstCacheService.delete(cacheNames);
        //2.再删除各个节点的本地缓存
        clearAndSend(cacheNames);
        return result;
    }

    /**
     * 清理缓存（支持批量清理）
     *
     * @param cacheNames
     */
    private void clearAndSend(String[] cacheNames) {
        // 先清理当前节点自己的本地缓存
        for (String cacheName : cacheNames) {
            doClearAndSend(cacheName, null, false);
        }
        // 再发送Redis缓存更新消息，清理各个节点的本地缓存
        notifyService.sendMessage(cacheNames, null);
    }

    /**
     * 保存并且发送缓存（支持批量清理）更新的通知到其他节点
     *
     * @param cacheNames
     * @param key
     */
    private void saveAndSend(String[] cacheNames, Object key, Object cacheValue) {
        //1.先保存到当前节点自己的本地缓存
        for (String cacheName : cacheNames) {
            doSaveAndSend(cacheName, key, cacheValue, false);
        }
        //2.发送Redis缓存更新消息,目的是清除其他节点上的本地缓存，而非更新，这样更容易保证一致性！！！
        // 所有cacheNames统一发送
        notifyService.sendMessage(cacheNames, key);
    }

    /**
     * 清理缓存（支持批量清理）并通知其他节点以清除
     *
     * @param cacheNames
     * @param key
     */
    private void clearAndSend(String[] cacheNames, Object key) {
        //1.先清理当前节点自己的本地缓存
        for (String cacheName : cacheNames) {
            doClearAndSend(cacheName, key, false);
        }
        //2.再发送Redis缓存更新消息，目的是清除其他节点上的本地缓存
        notifyService.sendMessage(cacheNames, key);
    }

    /**
     * 保存本地缓存
     *
     * @param cacheName
     * @param key
     */
    private void doSaveAndSend(String cacheName, Object key, Object value, boolean isNeedSend) {
        // 获取缓存对象
        Cache caffeineCache = cacheMap.get(cacheName);
        if (caffeineCache == null) {
            caffeineCache = caffeineCache();
            cacheMap.putIfAbsent(cacheName, caffeineCache);
        }
        caffeineCache.put(key, value);

        if (isNeedSend) {
            // 发送Redis缓存更新消息
            notifyService.sendMessage(cacheName, key);
        }
    }

    /**
     * 清除本地缓存
     *
     * @param cacheName
     * @param key
     */
    private void doClearAndSend(String cacheName, Object key, boolean isNeedSend) {
        // 获取缓存对象
        Cache caffeineCache = cacheMap.get(cacheName);
        if (caffeineCache == null) {
            return;
        }

        if (key == null) {
            // key键值为空， 则清空该缓存下面的所有条目
            caffeineCache.invalidateAll();
        } else {
            // 清除指定键值的缓存
            caffeineCache.invalidate(key);
        }

        //需要发送时才发送，触发删除操作的节点才需要发送以通知给其他节点进行删除！
        if (isNeedSend) {
            // 发送Redis缓存更新消息
            notifyService.sendMessage(cacheName, key);
        }
    }

}
