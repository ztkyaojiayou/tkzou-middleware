package com.tkzou.middleware.rpc.framework.protocol;

import com.tkzou.middleware.rpc.framework.MethodInvocation;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http请求客户端
 * 用于发送http请求并获取结果
 *
 * @author zoutongkun
 */
public class HttpClient {
    /**
     * 发送http请求
     *
     * @param hostname
     * @param port
     * @param methodInvocation
     * @return
     */
    public String send(String hostname, Integer port, MethodInvocation methodInvocation) {

        try {
            //发起http调用，这也是rpc的本质，本质还是一次http请求
            URL url = new URL("http", hostname, port, "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //post请求
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            //写入要调用的方法的元数据信息，后面通过反射调用具体的实现类中的该方法
            oos.writeObject(methodInvocation);
            oos.flush();
            oos.close();

            //获取请求结果
            InputStream inputStream = httpURLConnection.getInputStream();
            //我们这里默认就是String，即方法的返回值就使用String作demo
            return IOUtils.toString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
