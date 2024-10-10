package com.tkzou.middleware.transaction.mqretry.v2.dto;

/**
 * 消息实体接口
 *
 * @author zoutongkun
 */
public interface ISponsorAccountMessage {
    /**
     * 获取用户id
     *
     * @return
     */
    Long getUserId();

    /**
     * 获取业务id
     *
     * @return
     */
    Long getBusinessId();

    /**
     * 获取业务时间
     *
     * @return
     */
    Long getBusinessTime();
}
