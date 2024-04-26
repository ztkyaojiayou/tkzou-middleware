package com.tkzou.middleware.binlog.test.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * binlog中数据库表对应的实体
 *
 * @author zoutongkun
 */
@Data
public class BinlogTestDTO {

    private String id;

    private LocalDateTime createTime;

}
