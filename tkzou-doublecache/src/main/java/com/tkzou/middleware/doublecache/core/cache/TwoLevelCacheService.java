package com.tkzou.middleware.doublecache.core.cache;

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
public class TwoLevelCacheService implements DoubleCacheService {

    private static final Logger logger = LoggerFactory.getLogger(TwoLevelCacheService.class);

    /**
     * 二级缓存
     */
    private DoubleCacheService secondDoubleCacheService;

    /**
     * Caffeine内部缓存
     */
    private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * Redis 发送服务接口
     * 用于保持缓存的一致性
     */
    private NotifyService notifyService;

    /**
     * 缓存配置参数
     */
    private DoubleCacheConfig doubleCacheConfig;


    public TwoLevelCacheService(DoubleCacheService secondDoubleCacheService,
                                NotifyService notifyService,
                                DoubleCacheConfig doubleCacheConfig) {
        this.secondDoubleCacheService = secondDoubleCacheService;
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
            result = secondDoubleCacheService.get(cacheName, cacheKey);
            logger.info("getFromCache # fetch data from redis cache.");
            // 3.再保存更新Caffeine缓存
            saveCaffeineCache(cacheName, cacheKey, result, caffeineCache);
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
        //先写到redis
        boolean result = secondDoubleCacheService.save(cacheNames, cacheKey, cacheValue, ttl);
        // 再保存并广播更新二级缓存
        saveAndSend(cacheNames, cacheKey, cacheValue);
        return result;
    }

    @Override
    public boolean saveByAsync(String[] cacheNames, Object cacheKey, Object cacheValue, long ttl) {
        boolean result = secondDoubleCacheService.saveByAsync(cacheNames, cacheKey, cacheValue, ttl);
        // 保存并广播更新二级缓存
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
        boolean result = secondDoubleCacheService.delete(cacheNames, cacheKey);
        clearAndSend(cacheNames, cacheKey);
        return result;
    }

    @Override
    public boolean delete(String[] cacheNames) {
        boolean result = secondDoubleCacheService.delete(cacheNames);
        clearAndSend(cacheNames);
        return result;
    }

    /**
     * 清理缓存（支持批量清理）
     *
     * @param cacheNames
     */
    private void clearAndSend(String[] cacheNames) {
        for (String cacheName : cacheNames) {
            doClearAndSend(cacheName, null, false);
        }
        // 发送Redis缓存更新消息
        notifyService.sendMessage(cacheNames, null);
    }

    /**
     * 保存并且发送缓存（支持批量清理）
     *
     * @param cacheNames
     * @param key
     */
    private void saveAndSend(String[] cacheNames, Object key, Object cacheValue) {
        for (String cacheName : cacheNames) {
            doSaveAndSend(cacheName, key, cacheValue, false);
        }
        // 发送Redis缓存更新消息, 所有cacheNames统一发送
        notifyService.sendMessage(cacheNames, key);
    }

    /**
     * 清理缓存（支持批量清理）
     *
     * @param cacheNames
     * @param key
     */
    private void clearAndSend(String[] cacheNames, Object key) {
        for (String cacheName : cacheNames) {
            doClearAndSend(cacheName, key, false);
        }
        // 发送Redis缓存更新消息
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

        if (isNeedSend) {
            // 发送Redis缓存更新消息
            notifyService.sendMessage(cacheName, key);
        }
    }

}
