package com.tkzou.middleware.binlog.core.persistence;

import com.alibaba.fastjson.JSON;
import com.tkzou.middleware.binlog.core.BinlogPositionHandler;
import com.tkzou.middleware.binlog.core.config.RedisConfig;
import com.tkzou.middleware.binlog.core.exception.BinlogException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author zoutongkun
 */
@Slf4j
public class RedisBinlogPositionHandler implements BinlogPositionHandler {

    private final JedisPool jedisPool;

    public RedisBinlogPositionHandler(RedisConfig redisConfig) {
        this.jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), redisConfig.getHost(), redisConfig.getPort(), 1000, redisConfig.getPassword(), redisConfig.getDatabase());
    }

    @Override
    public BinlogPosition getLastPosition(Long serverId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(serverId.toString());
            if (value != null) {
                return JSON.parseObject(value, BinlogPosition.class);
            }
        } catch (JedisConnectionException e) {
            throw new BinlogException("Unable to connect to Redis host.");
        }
        return null;
    }

    @Override
    public void saveCurPosition(BinlogPosition position) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(position.getServerId().toString(), JSON.toJSONString(position));
        }
    }
}
