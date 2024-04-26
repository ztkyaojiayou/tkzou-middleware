package com.tkzou.middleware.binlog.test.handler;


import com.tkzou.middleware.binlog.core.IBinlogEventHandler;
import com.tkzou.middleware.binlog.core.event.BinlogEvent;
import com.tkzou.middleware.binlog.starter.annotation.BinlogSubscriber;
import com.tkzou.middleware.binlog.test.domain.BinlogTestDTO;

/**
 * 一个具体的binlog处理器
 *
 * @author zoutongkun
 */
@BinlogSubscriber(clientName = "master")
public class BinlogTestEventHandler implements IBinlogEventHandler<BinlogTestDTO> {

    @Override
    public void onInsert(BinlogEvent<BinlogTestDTO> event) {
        System.out.println("插入数据:" + event.getData());
    }

    @Override
    public void onUpdate(BinlogEvent<BinlogTestDTO> event) {
        System.out.println("修改数据:" + event.getData());
    }

    @Override
    public void onDelete(BinlogEvent<BinlogTestDTO> event) {
        System.out.println("删除数据:" + event.getData());
    }

    @Override
    public boolean isHandle(String database, String table) {
        return true;
    }
}
