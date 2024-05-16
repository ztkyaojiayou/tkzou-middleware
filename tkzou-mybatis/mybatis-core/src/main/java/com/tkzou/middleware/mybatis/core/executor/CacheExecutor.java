package com.tkzou.middleware.mybatis.core.executor;

import com.tkzou.middleware.mybatis.core.cache.Cache;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;

import java.util.List;

/**
 * <p> 缓存执行器 </p>
 * 使用了装饰器模式！
 * 用于装饰SimpleExecutor
 * 作用是管理二级缓存
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/28 05:31
 */
public class CacheExecutor implements Executor {
    /**
     * 原执行器，属于装饰器模式
     */
    private Executor delegate;

    public CacheExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> List<T> query(MappedStatement ms, Object parameter) {
        //先取二级缓存
        Cache cache = ms.getCache();
        //构建缓存key
        String cacheKey = ms.createCacheKey(parameter);
        //从二级缓存中取数据
        Object cacheData = cache.getObject(cacheKey);
        if (cacheData != null) {
            return (List<T>) cacheData;
        }
        List<Object> list = this.delegate.query(ms, parameter);
        //todo 这个实现是有问题的，这里只是简单实现一下
        // 因为二级缓存是跨session的，也即是跨事务的，
        // 因此只有当前一个session事务提交之后才能把结果存入二级缓存，否则就会有脏数据的产生，
        // 因此需要先使用一个临时变量保存一下前一个session的查询结果，
        // 等到事务提交之后再真正存入二级缓存
        cache.putObject(cacheKey, list);
        return (List<T>) list;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) {
        //先清理二级缓存，也是全部清除！
        ms.getCache().clear();
        return this.delegate.update(ms, parameter);
    }

    @Override
    public void commit() {
        this.delegate.commit();
    }

    @Override
    public void rollback() {
        this.delegate.rollback();
    }

    @Override
    public void close() {
        this.delegate.close();
    }
}
