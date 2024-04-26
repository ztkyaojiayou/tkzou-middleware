package com.tkzou.middleware.binlog.core;

/**
 * Binlog 客户端接口
 * 用户使用时的入口
 * 后续在使用springboot-starter的方式集成时也就是调用这个接口进行注册
 * 只是初始化的时机纳入springboot管理！
 * 本质就是通过springboot在启动时提供的各种扩展点来集成接入！
 *
 * @author zoutongkun
 * *
 */
public interface IBinlogClient {

    /**
     * 开始连接到mysql
     */
    void connect();

    /**
     * 注册 binlog event 处理器
     * key随机
     *
     * @param eventHandler 事件处理器
     */
    void registerEventHandler(IBinlogEventHandler eventHandler);

    /**
     * 注册 binlog event 处理器
     *
     * @param handlerKey   具名 Key
     * @param eventHandler 事件处理器
     */
    void registerEventHandler(String handlerKey, IBinlogEventHandler eventHandler);

    /**
     * 注销 binlog event 处理器
     *
     * @param handlerKey 具名 Key
     */
    void unregisterEventHandler(String handlerKey);

    /**
     * 断开连接
     */
    void disconnect();

}
