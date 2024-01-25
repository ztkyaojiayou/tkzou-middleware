//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tkzou.middleware.tomcat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zoutongkun
 */
@WebServlet(
        urlPatterns = {"/test"}
)
public class MyServlet extends HttpServlet {
    public MyServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.getMethod());
        resp.getOutputStream().write("hello tomcat".getBytes());
    }
}
