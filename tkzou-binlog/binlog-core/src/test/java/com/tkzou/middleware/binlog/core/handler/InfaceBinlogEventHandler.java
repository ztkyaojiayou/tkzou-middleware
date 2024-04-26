package com.tkzou.middleware.binlog.core.handler;

import com.tkzou.middleware.binlog.core.IBinlogEventHandler;
import com.tkzou.middleware.binlog.core.domain.BinlogDto;
import com.tkzou.middleware.binlog.core.event.BinlogEvent;

/**
 * 一个具体的binlog事件处理器
 */
public class InfaceBinlogEventHandler implements IBinlogEventHandler<BinlogDto> {

    @Override
    public void onInsert(BinlogEvent<BinlogDto> event) {
        System.out.println("插入事件:" + event.getData());
    }

    @Override
    public void onUpdate(BinlogEvent<BinlogDto> event) {
        System.out.println("修改事件:" + event.getData());
    }

    @Override
    public void onDelete(BinlogEvent<BinlogDto> event) {
        System.out.println("删除事件:" + event.getData());
    }

    @Override
    public boolean isHandle(String database, String table) {
        return true;
    }

}
