package com.tkzou.middleware.binlog.core.handler;

import com.tkzou.middleware.binlog.core.IBinlogEventHandler;

/**
 * binlog事件处理器抽象类
 *
 * @author zoutongkun
 */
public abstract class AbstractBinlogEventHandler<T> implements IBinlogEventHandler<T> {
    /**
     * 基础实现
     * 默认true
     *
     * @param database
     * @param table
     * @return
     */
    @Override
    public boolean isHandle(String database, String table) {
        return true;
    }
}