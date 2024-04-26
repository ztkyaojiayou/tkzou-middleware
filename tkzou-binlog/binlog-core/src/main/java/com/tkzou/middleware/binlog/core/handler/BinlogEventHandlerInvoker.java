package com.tkzou.middleware.binlog.core.handler;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.tkzou.middleware.binlog.core.IBinlogEventHandler;
import com.tkzou.middleware.binlog.core.common.meta.ColumnMetadata;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.core.event.BinlogEvent;
import com.tkzou.middleware.binlog.core.utils.ClassUtil;
import com.tkzou.middleware.binlog.core.utils.JdbcUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Binlog 处理器包装类
 *
 * @author zoutongkun
 */
@Data
public class BinlogEventHandlerInvoker<T> {
    /**
     * 每张表对应的列信息
     * 这里相当于是使用了本地缓存进行存储
     */
    private Map<String, List<ColumnMetadata>> columnMetadataMap = new HashMap<>();
    /**
     * 具体的处理器
     */
    private IBinlogEventHandler eventHandler;

    private BinlogClientConfig clientConfig;

    private Class<T> genericClass;

    private static final ParserConfig SNAKE_CASE;

    static {
        SNAKE_CASE = new ParserConfig();
        SNAKE_CASE.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 处理插入操作的变更信息
     *
     * @param databaseName
     * @param tableName
     * @param data
     */
    public void invokeInsert(String databaseName, String tableName, List<Serializable[]> data) {
        if (eventHandler.isHandle(databaseName, tableName)) {
            List<ColumnMetadata> columns = getColumns(databaseName, tableName);
            BinlogEvent binlogEvent = createBinlogEvent(databaseName, tableName);
            data.forEach(row -> {
                binlogEvent.setData(toEntity(columns, row));
                eventHandler.onInsert(binlogEvent);
            });
        }
    }

    /**
     * 处理更新操作的变更信息
     *
     * @param databaseName
     * @param tableName
     * @param data
     */
    public void invokeUpdate(String databaseName, String tableName, List<Map.Entry<Serializable[], Serializable[]>> data) {
        //是否要处理，这里到表级别--属于配置项
        if (eventHandler.isHandle(databaseName, tableName)) {
            List<ColumnMetadata> columns = getColumns(databaseName, tableName);
            //封装成自定义的BinlogEvent
            BinlogEvent binlogEvent = createBinlogEvent(databaseName, tableName);
            data.forEach(row -> {
                //更新时binlog会同时记录原始数据和更新后的数据，具体为：key为原数据，value为更新后的数据
                //参考：https://juejin.cn/post/6992437804240207886
                binlogEvent.setData(toEntity(columns, row.getValue()));
                binlogEvent.setOriginalData(toEntity(columns, row.getKey()));
                eventHandler.onUpdate(binlogEvent);
            });
        }
    }

    /**
     * 处理删除操作的变更信息
     *
     * @param databaseName
     * @param tableName
     * @param data
     */
    public void invokeDelete(String databaseName, String tableName, List<Serializable[]> data) {
        if (eventHandler.isHandle(databaseName, tableName)) {
            List<ColumnMetadata> columns = getColumns(databaseName, tableName);
            BinlogEvent binlogEvent = createBinlogEvent(databaseName, tableName);
            data.forEach(row -> {
                //把实际数据塞进去
                binlogEvent.setData(toEntity(columns, row));
                eventHandler.onDelete(binlogEvent);
            });
        }
    }

    private BinlogEvent createBinlogEvent(String databaseName, String tableName) {
        BinlogEvent binlogEvent = new BinlogEvent<>();
        binlogEvent.setDatabase(databaseName);
        binlogEvent.setTable(tableName);
        binlogEvent.setTimestamp(System.currentTimeMillis());
        return binlogEvent;
    }

    /**
     * 获取指定表的列信息
     * 需要连接数据库查一下
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    public List<ColumnMetadata> getColumns(String databaseName, String tableName) {
        String tableSchema = String.format("%s.%s", databaseName, tableName);
        //先查是否已经存在本地缓存了，若有，则直接返回，否则才去mysql查
        List<ColumnMetadata> columns = columnMetadataMap.get(tableSchema);
        if (columns == null || clientConfig.isStrict()) {
            //去mysql查，同时保存到本地缓存，先不考虑数据库列变更的情况
            columns = JdbcUtil.getColumns(clientConfig, databaseName, tableName);
            columnMetadataMap.put(tableSchema, columns);
        }
        return columns;
    }

    /**
     * 转实体，使用反射
     *
     * @param columns
     * @param data    来自binlog的数据，结构可参考：https://juejin.cn/post/6992437804240207886
     * @return 返回值就是当前表对应的实体，也就在BinlogEvent类中定义的泛型类！
     */
    @SneakyThrows
    public T toEntity(List<ColumnMetadata> columns, Serializable[] data) {
        //先使用map接收
        Map<String, Object> obj = new HashMap<>(columns.size());
        for (int i = 0; i < data.length; i++) {
            ColumnMetadata column = columns.get(i);
            Serializable fieldValue = data[i];
            if (fieldValue instanceof Date) {
                data[i] = new Date(((Date) fieldValue).getTime() + clientConfig.getTimeOffset());
            } else if (fieldValue instanceof byte[]) {
                if (genericClass != null) {
                    Field field = ClassUtil.getDeclaredField(genericClass, column.getColumnName());
                    if (field != null) {
                        if (field.getType() == String.class) {
                            data[i] = new String((byte[]) fieldValue, StandardCharsets.UTF_8);
                        }
                    }
                }
            } else if (fieldValue instanceof BitSet) {
                if (genericClass != null) {
                    Field field = ClassUtil.getDeclaredField(genericClass, column.getColumnName());
                    if (field != null) {
                        if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                            data[i] = !((BitSet) fieldValue).isEmpty();
                        }
                    }
                }
            }
            obj.put(column.getColumnName(), data[i]);
        }
        if (genericClass != null) {
            return TypeUtils.cast(obj, genericClass, SNAKE_CASE);
        }
        //2.再强转为目标类
        return (T) obj;
    }

    public void setEventHandler(IBinlogEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.genericClass = ClassUtil.getGenericType(eventHandler.getClass());
    }
}
