package com.tkzou.middleware.threadpool.simple.v4;

/**
 * 拒绝策略
 *
 * @author zoutongkun
 */

public enum RejectPolicy {

    ABORT,
    CALLER_RUN,
    DISCARD_OLDEST,
    DISCARD
}