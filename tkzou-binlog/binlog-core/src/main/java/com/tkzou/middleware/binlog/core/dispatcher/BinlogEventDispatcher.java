package com.tkzou.middleware.binlog.core.dispatcher;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.tkzou.middleware.binlog.core.BinlogPositionHandler;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.core.handler.BinlogEventHandlerInvoker;
import com.tkzou.middleware.binlog.core.persistence.BinlogPosition;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心监听器，用于监听最原始的sql变更信息
 *
 * @author zoutongkun
 */
public class BinlogEventDispatcher implements BinaryLogClient.EventListener {
    /**
     * 表信息map
     */
    private final Map<Long, TableMapEventData> tableMap = new HashMap<>();
    /**
     * binlog事件处理器map
     */
    private final Map<String, BinlogEventHandlerInvoker> eventHandlerMap;

    private final BinlogClientConfig clientConfig;
    /**
     * binlog事件持久化处理器
     */
    private final BinlogPositionHandler binlogPositionHandler;

    public BinlogEventDispatcher(BinlogClientConfig clientConfig, BinlogPositionHandler positionHandler, Map<String, BinlogEventHandlerInvoker> eventHandlerMap) {
        this.clientConfig = clientConfig;
        this.eventHandlerMap = eventHandlerMap;
        this.binlogPositionHandler = positionHandler;
    }

    /**
     * 核心方法，用于接收bingLog变更事件
     * 只要注册了当前类，就会开始监听！！！
     * 无需加诸如@xxxListenner注解，
     * 注解基本都是在springboot中的玩法！！！
     *
     * @param event 所有的binlog写事件
     */
    @Override
    public void onEvent(Event event) {
        EventHeaderV4 headerV4 = event.getHeader();
        EventType eventType = headerV4.getEventType();
        //1.先收集表名id和表名的映射--TableMapEventData
        if (eventType == EventType.TABLE_MAP) {
            TableMapEventData eventData = event.getData();
            tableMap.put(eventData.getTableId(), eventData);
        } else {
            //2.再解析写变更数据，即增删改的数据--RowMutationEventData
            if (EventType.isRowMutation(eventType)) {
                //2.1实际数据，封装了所有写操作变更对应的数据
                RowMutationEventData rowMutationEventData = new RowMutationEventData(event.getData());
                //2.2所属表信息，需要配套使用，不然无法得知当前数据属于哪个表
                TableMapEventData tableMapEventData = tableMap.get(rowMutationEventData.getTableId());
                if (tableMapEventData != null) {
                    String database = tableMapEventData.getDatabase();
                    String table = tableMapEventData.getTable();
                    this.eventHandlerMap.forEach((handlerKey, eventHandler) -> {
                        //3.再细分具体的写操作，分发到对应的处理器进行处理
                        //更新
                        if (EventType.isUpdate(eventType)) {
                            eventHandler.invokeUpdate(database, table, rowMutationEventData.getUpdateRows());
                            return;
                        }
                        //删除
                        if (EventType.isDelete(eventType)) {
                            eventHandler.invokeDelete(database, table, rowMutationEventData.getDeleteRows());
                            return;
                        }
                        //插入
                        if (EventType.isWrite(eventType)) {
                            eventHandler.invokeInsert(database, table, rowMutationEventData.getInsertRows());
                        }
                    });
                }
            }
        }

        //4.处理完后维护一下宕机续读机制
        if (clientConfig.getPersistence()) {
            if (binlogPositionHandler != null) {
                if (eventType != EventType.FORMAT_DESCRIPTION) {
                    BinlogPosition binlogPosition = new BinlogPosition();
                    //ROTATE：轮流模式，保存一下
                    if (EventType.ROTATE == eventType) {
                        binlogPosition.setServerId(clientConfig.getServerId());
                        binlogPosition.setFilename(((RotateEventData) event.getData()).getBinlogFilename());
                        binlogPosition.setPosition(((RotateEventData) event.getData()).getBinlogPosition());
                    } else {
                        binlogPosition = binlogPositionHandler.getLastPosition(clientConfig.getServerId());
                        if (binlogPosition != null) {
                            //其他模式：更新下次处理的位置
                            binlogPosition.setPosition(headerV4.getNextPosition());
                        }
                    }
                    //保存一下
                    binlogPositionHandler.saveCurPosition(binlogPosition);
                }
            }
        }
    }

    /**
     * 具体的数据
     */
    @Data
    public static class RowMutationEventData {
        /**
         * 所属表id
         */
        private long tableId;
        /**
         * 插入时的binlog日志数据
         */
        private List<Serializable[]> insertRows;
        /**
         * 删除时的binlog日志数据
         */
        private List<Serializable[]> deleteRows;
        /**
         * 更新时的binlog日志数据
         */
        private List<Map.Entry<Serializable[], Serializable[]>> updateRows;

        public RowMutationEventData(EventData eventData) {
            //更新时的数据
            if (eventData instanceof UpdateRowsEventData) {
                UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) eventData;
                this.tableId = updateRowsEventData.getTableId();
                this.updateRows = updateRowsEventData.getRows();
                return;
            }

            //插入时的数据
            if (eventData instanceof WriteRowsEventData) {
                WriteRowsEventData writeRowsEventData = (WriteRowsEventData) eventData;
                this.tableId = writeRowsEventData.getTableId();
                this.insertRows = writeRowsEventData.getRows();
                return;
            }

            //删除时的数据
            if (eventData instanceof DeleteRowsEventData) {
                DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
                this.tableId = deleteRowsEventData.getTableId();
                this.deleteRows = deleteRowsEventData.getRows();
            }
        }
    }
}
