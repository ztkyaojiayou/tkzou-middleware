package com.tkzou.middleware.rpc.framework.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求处理器
 *
 * @author zoutongkun
 */
public interface ServletHandler {
    /**
     * 请求处理，主要是对来自客户端的请求进行处理，并返回响应。
     * 也即处理rpc请求
     *
     * @param req
     * @param resp
     */
    void handler(HttpServletRequest req, HttpServletResponse resp);
}
