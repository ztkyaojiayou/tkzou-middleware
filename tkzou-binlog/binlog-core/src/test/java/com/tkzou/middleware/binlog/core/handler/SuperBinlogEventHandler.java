package com.tkzou.middleware.binlog.core.handler;

import com.tkzou.middleware.binlog.core.event.BinlogEvent;

/**
 * 一个具体的binlog事件处理器
 */
public class SuperBinlogEventHandler extends AbstractBinlogEventHandler {

    @Override
    public void onInsert(BinlogEvent event) {
        System.out.println("插入事件:" + event.getData());
    }

    @Override
    public void onUpdate(BinlogEvent event) {
        System.out.println("修改事件:" + event.getData());
    }

    @Override
    public void onDelete(BinlogEvent event) {
        System.out.println("删除事件:" + event.getData());
    }

    @Override
    public boolean isHandle(String database, String table) {
        return database.equals("pear-admin") && table.equals("user");
    }
}
