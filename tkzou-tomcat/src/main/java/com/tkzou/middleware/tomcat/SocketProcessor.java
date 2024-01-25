package com.tkzou.middleware.tomcat;

import com.tkzou.middleware.tomcat.context.Context;
import com.tkzou.middleware.tomcat.request.MyRequest;
import com.tkzou.middleware.tomcat.response.MyResponse;
import com.tkzou.middleware.tomcat.servlettest.DefaultServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author zoutongkun
 */
public class SocketProcessor implements Runnable {

    private Socket socket;
    private MyTomcat myTomcat;

    public SocketProcessor(Socket socket, MyTomcat myTomcat) {
        this.socket = socket;
        this.myTomcat = myTomcat;
    }

    @Override
    public void run() {
        processSocket(socket);
    }

    private void processSocket(Socket socket) {
        // 处理Socket连接 读数据 写数据
        try {
            InputStream inputStream = socket.getInputStream();

            byte[] bytes = new byte[1024];
            inputStream.read(bytes);

            // 解析字节流，遇到一个空格就退出
            int pos = 0;
            int begin = 0, end = 0;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == ' ') {
                    break;
                }
            }

            // 组合空格之前的字节流，转换成字符串就是请求方法
            StringBuilder method = new StringBuilder();
            for (; begin < end; begin++) {
                method.append((char) bytes[begin]);
            }

            pos++;
            begin++;
            end++;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == ' ') break;
            }
            StringBuilder url = new StringBuilder();
            for (; begin < end; begin++) {
                url.append((char) bytes[begin]);
            }

            // 解析协议版本
            pos++;
            begin++;
            end++;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == '\r') break;
            }
            StringBuilder protocl = new StringBuilder();
            for (; begin < end; begin++) {
                protocl.append((char) bytes[begin]);
            }

            MyRequest myRequest = new MyRequest(method.toString(), url.toString(), protocl.toString(), socket);
            MyResponse myResponse = new MyResponse(myRequest);

            String requestUrl = myRequest.getRequestURL().toString();
            System.out.println(requestUrl);
            requestUrl = requestUrl.substring(1);
            String[] parts = requestUrl.split("/");

            String appName = parts[0];
            Context context = myTomcat.getContextMap().get(appName);

            if (parts.length > 1) {
                Servlet servlet = context.getByUrlPattern(parts[1]);

                if (servlet != null) {
                    servlet.service(myRequest, myResponse);
                    // 发送响应
                    myResponse.complete();
                } else {
                    DefaultServlet defaultServlet = new DefaultServlet();
                    defaultServlet.service(myRequest, myResponse);
                    // 发送响应
                    myResponse.complete();
                }
            }

        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

}
