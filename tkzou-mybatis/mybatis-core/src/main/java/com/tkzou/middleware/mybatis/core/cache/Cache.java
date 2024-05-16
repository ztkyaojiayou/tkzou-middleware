package com.tkzou.middleware.mybatis.core.cache;

/**
 * <p> 缓存 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/28 04:42
 */
public interface Cache {

    String getId();

    void putObject(Object key, Object value);

    Object getObject(Object key);

    Object removeObject(Object key);

    void clear();


}
