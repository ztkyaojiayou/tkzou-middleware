package com.tkzou.middleware.binlog.core.persistence;

import lombok.ToString;

/**
 * binlog持久化，用于下次处理
 * 持久化什么？当前服务已经处理到哪个binlog文件了
 * 持久化到哪里？redis
 *
 * @author zoutongkun
 */
@ToString
public class BinlogPosition {
    /**
     * 当前项目/服务id
     */
    private Long serverId;
    /**
     * 上次处理的binlog文件名所属的位置，唯一
     */
    private Long position;
    /**
     * 上次处理的binlog文件名
     */
    private String filename;

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
