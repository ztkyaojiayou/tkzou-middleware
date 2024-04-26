package com.tkzou.middleware.binlog.core.common.meta;

import lombok.Data;

/**
 * mysql中数据列的元信息
 *
 * @author zoutongkun
 */
@Data
public class ColumnMetadata {

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列类型
     */
    private String dataType;

    /**
     * 字符集
     */
    private String characterSetName;
}
