package com.tkzou.middleware.sms.core.dao;

import cn.hutool.core.thread.ThreadUtil;
import com.tkzou.middleware.sms.exception.SmsException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DAO 默认实现（内部缓存）
 * 只有没有用户自定义的实现时才注入！
 * 使用@ConditionalOnMissingBean来控制注入时机
 *
 * @author zoutongkun
 * @author zoutongkun
 * @since 2024/8/5 20:36
 */
@Slf4j
@Component
@ConditionalOnMissingBean
public class DefaultSmsDao implements SmsDao {

    private static volatile DefaultSmsDao INSTANCE;

    /**
     * 定时线程池
     * 不用使用Timer啦！
     */
    private static final ScheduledExecutorService scheduleThreadPool = ThreadUtil.createScheduledExecutor(1);

    /**
     * 缓存数据，类比redis，只是这里使用内存存储而已！
     * 这里使用了ConcurrentHashMap保证线程安全
     */
    private static final ConcurrentHashMap<String, CacheData> CACHE_DATA_MAP = new ConcurrentHashMap<>();

    /**
     * 缓存时间（单位：毫秒，默认 24 小时）
     */
    private static final long DEFAULT_CACHE_TIME = 24 * 60 * 60 * 1000L;

    /**
     * 定时器执行频率（单位：毫秒，默认 30 秒）
     */
    private static final long TIMER_INTERVAL = 30L;

    /**
     * 获取单例
     * 双重检锁模式！
     *
     * @return 唯一实例
     */
    public static DefaultSmsDao getInstance() {
        if (null == INSTANCE) {
            synchronized (DefaultSmsDao.class) {
                if (null == INSTANCE) {
                    INSTANCE = new DefaultSmsDao();
                    // 同时开启数据清理
                    startClearExpiredData();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void set(String key, Object value, long cacheTime) {
        cacheTime = cacheTime * 1000L;
        CacheData cacheData = CACHE_DATA_MAP.get(key);
        if (null != cacheData) {
            cacheData.update(value, cacheTime);
        } else {
            CacheData newData = CacheData.create(value, cacheTime);
            CACHE_DATA_MAP.put(key, newData);
        }
    }

    @Override
    public void set(String key, Object value) {
        this.set(key, value, DEFAULT_CACHE_TIME);
    }

    @Override
    public Object get(String key) {
        CacheData cacheData = CACHE_DATA_MAP.get(key);
        if (cacheData != null && !cacheData.isExpired()) {
            return cacheData.data;
        }
        return null;
    }

    @Override
    public void clean() {
        CACHE_DATA_MAP.clear();
    }

    /**
     * 启动定时任务,定时清理过期的key
     */
    private static void startClearExpiredData() {
        //不推荐scheduleAtFixedRate方法
        scheduleThreadPool.scheduleWithFixedDelay(() -> {
            try {
                doClearExpiredData();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new SmsException(e.getMessage());
            }
            //每30秒监控一次！
        }, TIMER_INTERVAL, TIMER_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 清除过期数据
     */
    private static void doClearExpiredData() {
        //先临时保存一下所有失效key
        List<String> expiredKeyList = new LinkedList<>();
        //遍历所有的缓存数据，一个一个判断和处理
        for (Map.Entry<String, CacheData> entry : CACHE_DATA_MAP.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredKeyList.add(entry.getKey());
            }
        }
        //再清理
        for (String key : expiredKeyList) {
            CACHE_DATA_MAP.remove(key);
        }
    }

    /**
     * 缓存数据封装
     * 本来使用redis即可，但对于一个框架而言，通常只暴露接口，由用户自行实现，
     * 但框架自身一般也会有一个默认实现，此时一般就只是基于内存啦！
     * 当然也可以默认就集成redis。
     */
    @Data
    private static class CacheData {

        /**
         * 数据
         */
        private Object data;

        /**
         * 过期时间，不是时长，
         * 因为这里没有使用redis，因为需要自己维护过期机制，
         * 因此使用过期时间便于判断！
         */
        private long expiredTime;

        /**
         * 缓存时间
         */
        private long cacheTime;

        private CacheData(Object data, long cacheTime) {
            this.data = data;
            this.cacheTime = cacheTime;
            this.expiredTime = System.currentTimeMillis() + cacheTime;
        }

        /**
         * 新建数据和缓存数据
         *
         * @param data
         * @param cacheTime
         * @return
         */
        public static CacheData create(Object data, long cacheTime) {
            return new CacheData(data, cacheTime);
        }

        /**
         * 更新数据及缓存时间
         *
         * @param data      数据
         * @param cacheTime 缓存时间
         */
        public void update(Object data, long cacheTime) {
            this.data = data;
            this.cacheTime = cacheTime;
            this.expiredTime = System.currentTimeMillis() + cacheTime;
        }

        /**
         * 数据是否过期
         *
         * @return true：过期，false：未过期
         */
        public boolean isExpired() {
            if (this.expiredTime > 0) {
                //通过比较当前时间和要过期的时间即可，大于即为过期
                return System.currentTimeMillis() > this.expiredTime;
            }
            return true;
        }
    }
}
