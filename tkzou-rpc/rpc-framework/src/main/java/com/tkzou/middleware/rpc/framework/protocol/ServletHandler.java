package com.tkzou.middleware.rpc.framework.protocol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求处理器
 *
 * @author zoutongkun
 */
public interface ServletHandler {
    /**
     * 请求处理，主要是对
     *
     * @param req
     * @param resp
     */
    void handler(HttpServletRequest req, HttpServletResponse resp);
}
