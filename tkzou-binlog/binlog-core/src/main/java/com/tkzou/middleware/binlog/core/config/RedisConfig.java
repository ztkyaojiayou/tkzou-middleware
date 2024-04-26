package com.tkzou.middleware.binlog.core.config;

/**
 * redis配置项
 * 用于持久化
 *
 * @author zoutongkun
 */
public class RedisConfig {

    private String host;

    private int port = 6379;

    private String password;

    private int database;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
