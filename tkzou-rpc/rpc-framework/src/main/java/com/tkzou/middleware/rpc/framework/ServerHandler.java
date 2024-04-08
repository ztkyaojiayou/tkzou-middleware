package com.tkzou.middleware.rpc.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zoutongkun
 */
public interface ServerHandler {

    void handler(HttpServletRequest req, HttpServletResponse resp);
}
