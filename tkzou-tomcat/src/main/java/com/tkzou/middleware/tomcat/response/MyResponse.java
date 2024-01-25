package com.tkzou.middleware.tomcat.response;

import com.tkzou.middleware.tomcat.ResponseServletOutputStream;
import com.tkzou.middleware.tomcat.request.MyRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zoutongkun
 */
public class MyResponse extends AbstractHttpServletResponse {

    private int status = 200;
    private String message = "OK";
    private byte SP = ' ';
    private byte CR = '\r';
    private byte LF = '\n';
    private Map<String, String> headers = new HashMap<>();
    private MyRequest myRequest;
    private OutputStream socketOutputStream;
    private ResponseServletOutputStream responseServletOutputStream = new ResponseServletOutputStream();

    public MyResponse(MyRequest myRequest) {
        this.myRequest = myRequest;
        try {
            this.socketOutputStream = myRequest.getSocket().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatus(int i, String s) {
        status = i;
        message = s;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
    }

    @Override
    public ResponseServletOutputStream getOutputStream() throws IOException {
        return responseServletOutputStream;
    }

    public void complete() {
        // 发送响应
        try {
            sendResponseLine();
            sendResponseHeader();
            sendResponseBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendResponseBody() throws IOException {
        socketOutputStream.write(getOutputStream().getBytes());
    }

    private void sendResponseHeader() throws IOException {

        if (!headers.containsKey("Content-Length")) {
            addHeader("Content-Length", String.valueOf(getOutputStream().getPos()));
        }

        if (!headers.containsKey("Content-Type")) {
            addHeader("Content-Type", "text/plain;charset=utf-8");
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            socketOutputStream.write(key.getBytes());
            socketOutputStream.write(":".getBytes());
            socketOutputStream.write(value.getBytes());
            socketOutputStream.write(CR);
            socketOutputStream.write(LF);
        }
        socketOutputStream.write(CR);
        socketOutputStream.write(LF);
    }

    private void sendResponseLine() throws IOException {
        socketOutputStream.write(myRequest.getProtocol().getBytes());
        socketOutputStream.write(SP);
        socketOutputStream.write(status);
        socketOutputStream.write(SP);
        socketOutputStream.write(message.getBytes());
        socketOutputStream.write(CR);
        socketOutputStream.write(LF);
    }
}
