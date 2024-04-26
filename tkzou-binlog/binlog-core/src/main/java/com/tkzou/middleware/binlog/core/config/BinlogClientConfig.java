package com.tkzou.middleware.binlog.core.config;

import com.tkzou.middleware.binlog.core.BinlogPositionHandler;
import com.tkzou.middleware.binlog.core.common.enums.BinlogClientMode;
import com.tkzou.middleware.binlog.core.utils.Md5Util;
import com.zaxxer.hikari.HikariConfig;

import java.util.concurrent.TimeUnit;

/**
 * Binlog Client 配置
 * 与创建client分开
 *
 * @author zoutongkun
 */
public class BinlogClientConfig {

    /**
     * 账户
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private int port = 3306;

    /**
     * 时间偏移量
     */
    private long timeOffset = 0;

    /**
     * 客户端编号 (不同的集群)
     */
    private long serverId;

    /**
     * 是否保持连接
     */
    private boolean keepAlive = true;

    /**
     * 是否是首次启动
     */
    private boolean inaugural = false;

    /**
     * 保持连接时间
     */
    private long keepAliveInterval = TimeUnit.MINUTES.toMillis(1L);

    /**
     * 连接超时时间
     */
    private long connectTimeout = TimeUnit.SECONDS.toMillis(3L);

    /**
     * 发送心跳包时间间隔
     */
    private long heartbeatInterval = TimeUnit.SECONDS.toMillis(6L);

    /**
     * “分布式” “记忆读取”
     * <p>
     * 依赖的 Redis 中间件配置
     */
    private RedisConfig redisConfig;

    /**
     * 是否开启宕机续读
     * 即保证宕机期间的数据丢失, 从而保证数据一致性,
     * 每次处理 binlog event 时，都将会当前的消费进度记录到 Redis中,从而在下次启动时实现续读。
     */
    private boolean persistence = false;

    /**
     * 严格模式
     * <p>
     * 性能与健壮性的平衡
     */
    private boolean strict = true;

    /**
     * 部署模式
     */
    private BinlogClientMode mode = BinlogClientMode.standalone;

    /**
     * 持久化 PositionHandler 实现 (优先级 > RedisConfig)
     */
    private BinlogPositionHandler positionHandler;

    private HikariConfig hikariConfig;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public long getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }


    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Boolean getPersistence() {
        return persistence;
    }

    public void setPersistence(Boolean persistence) {
        this.persistence = persistence;
    }

    public BinlogClientMode getMode() {
        return mode;
    }

    public void setMode(BinlogClientMode mode) {
        this.mode = mode;
    }

    public String getKey() {
        return Md5Util.encrypt(this.host + ":" + this.port + ":" + this.serverId);
    }

    public long getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    public boolean isInaugural() {
        return inaugural;
    }

    public void setInaugural(boolean inaugural) {
        this.inaugural = inaugural;
    }

    public BinlogPositionHandler getPositionHandler() {
        return positionHandler;
    }

    public void setPositionHandler(BinlogPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public void setHikariConfig(HikariConfig hikariConfig) {
        this.hikariConfig = hikariConfig;
    }
}
