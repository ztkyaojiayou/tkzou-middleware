package com.tkzou.middleware.binlog.core;

import com.tkzou.middleware.binlog.core.event.BinlogEvent;

/**
 * Binlog 事件处理器接口
 *
 * @author zoutongkun
 */
public interface IBinlogEventHandler<T> {

    /**
     * 插入
     *
     * @param event 事件详情
     */
    void onInsert(BinlogEvent<T> event);

    /**
     * 修改
     *
     * @param event 事件详情
     */
    void onUpdate(BinlogEvent<T> event);

    /**
     * 删除
     *
     * @param event 事件详情
     */
    void onDelete(BinlogEvent<T> event);

    /**
     * 前置
     * <p>
     * 控制该 handler 是否处理当前 Binlog Event
     *
     * @param database
     * @param table
     * @return
     */
    boolean isHandle(String database, String table);

}
