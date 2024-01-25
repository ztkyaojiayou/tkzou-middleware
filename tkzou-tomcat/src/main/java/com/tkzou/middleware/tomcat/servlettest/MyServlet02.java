//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tkzou.middleware.tomcat.servlettest;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一个处理请求的servlet，需要把这个类编译后放入webapps下面，
 * 在tomcat启动时会加载该类，并将其存入context中以初始化！
 *
 * @author zoutongkun
 */
@WebServlet(
        urlPatterns = {"/test02"}
)
public class MyServlet02 extends HttpServlet {
    public MyServlet02() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.getMethod());
        resp.getOutputStream().write("hello tomcat---02".getBytes());
    }
}
