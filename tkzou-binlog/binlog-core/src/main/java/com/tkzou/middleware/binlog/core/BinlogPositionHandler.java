package com.tkzou.middleware.binlog.core;

import com.tkzou.middleware.binlog.core.persistence.BinlogPosition;

/**
 * 持久化处理器
 *
 * @author zoutongkun
 */
public interface BinlogPositionHandler {
    /**
     * 加载当前服务上次处理到的位置
     *
     * @param serverId
     * @return
     */
    BinlogPosition getLastPosition(Long serverId);

    /**
     * 保存当前服务已经处理完的位置
     *
     * @param position
     */
    void saveCurPosition(BinlogPosition position);
}
