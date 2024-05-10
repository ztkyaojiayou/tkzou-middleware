package com.tkzou.middleware.sms.core.dao;

/**
 * DAO 接口
 * 用于保存必要的数据，用户可自行实现，当前提供了基于内存的默认实现
 *
 * @author zoutongkun
 * @author zoutongkun
 * @since 2024/8/5 20:03
 */
public interface SmsDao {

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    void set(String key, Object value, long cacheTime);

    /**
     * 存储
     * 此时使用默认缓存时间
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 清空
     */
    void clean();
}
