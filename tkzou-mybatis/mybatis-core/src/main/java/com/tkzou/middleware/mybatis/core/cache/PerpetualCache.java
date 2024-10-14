package com.tkzou.middleware.mybatis.core.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * <p> 一级缓存，永久缓存 </p>
 * 范围：一个session内，因此也就不存在分布式环境下的数据不一致问题！！！
 * 同时，它对于缓存的维护规则为：
 * 只要是增删改，就清除该缓存，且不只是针对当前表的增删改，任何增删改都会触发，
 * 且清除的是整个map，而不只是某一个key的缓存！
 * 而对于二级缓存，因为是跨session的，相当于是真正的本地缓存，
 * 因此就存在分布式下的数据一致性问题，也因此一般都不开启！
 * 在mybatis中，二级缓存需要手动开启和配置，它是namespace级别的，也即是mapper接口级别的！
 * 参考：https://blog.csdn.net/x_023/article/details/136377494
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/28 04:43
 */
public class PerpetualCache implements Cache {
    /**
     * 唯一标识，不重要
     */
    private String id;
    /**
     * 缓存map
     * 这个key的设计很重要，目的就是唯一标识当前查询语句
     * 源码中key的结构：id + offset + limit + sql + param value + environment id。
     * 这里我们简化一下，直接使用MappedStatement中的id+sql语句+参数作为key了
     */
    private final Map<Object, Object> cacheMap = new HashMap<>();

    public PerpetualCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        this.cacheMap.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return this.cacheMap.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return this.cacheMap.remove(key);
    }

    @Override
    public void clear() {
        this.cacheMap.clear();
    }
}
