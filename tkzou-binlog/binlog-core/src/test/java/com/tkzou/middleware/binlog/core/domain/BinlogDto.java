package com.tkzou.middleware.binlog.core.domain;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.BitSet;

/**
 * 要监听的具体表对应的实体
 */
@Data
public class BinlogDto {

    private String username;

    private String password;

    private String remark;

    private Boolean sex;

    private LocalDateTime createTime;

    private Date createDate;

    private LocalDateTime updateTime;

    private LocalDate updateDate;

    private String longRemark;

    private BitSet bit;

    private byte[] byteRemark;

}
