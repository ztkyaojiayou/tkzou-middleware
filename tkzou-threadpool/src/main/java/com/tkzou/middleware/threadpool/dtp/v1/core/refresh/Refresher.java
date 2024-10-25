package com.tkzou.middleware.threadpool.dtp.v1.core.refresh;

/**
 * @author zoutongkun
 * @Date 2023/5/21 14:43
 */
public interface Refresher {
    /**
     * 刷新配置
     *
     * @param content
     */
    void refresh(String content);
}
