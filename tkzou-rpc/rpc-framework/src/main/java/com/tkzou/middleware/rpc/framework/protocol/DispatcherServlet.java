package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.HttpServletHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet调度器，用于整体调度tomcat请求
 * 最终是委托给ServletHandler接口实现，目的是可以按照功能分工
 *
 * @author zoutongkun
 */
public class DispatcherServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        new HttpServletHandler().handler(req, resp);
    }

}
