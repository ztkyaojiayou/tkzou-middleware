package com.tkzou.middleware.tomcat.core;

import cn.hutool.core.thread.ThreadUtil;
import com.tkzou.middleware.tomcat.context.Context;
import com.tkzou.middleware.tomcat.support.WebappClassLoader;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * tomcat主类
 *
 * @author zoutongkun
 */
public class MyTomcat {

    private Map<String, Context> contextMap = new HashMap<>();

    /**
     * 部署服务
     */
    public void deployApps() {
        //拼接出当前模块所在的路径
        String curPath = System.getProperty("user.dir") + "/tkzou-tomcat";
        File webapps = new File(curPath, "webapps");
        for (String app : Objects.requireNonNull(webapps.list())) {
            deployApp(webapps, app);
        }
    }

    /**
     * 启动tomcat
     */
    public void start() {
        // Socket连接 TCP
        try {
            ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(5, 10);
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true) {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(new SocketProcessor(socket, this));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deployApp(File webapps, String appName) {
        // 有哪些Servlet
        //保存所有的servlet，它就是用于实际处理请求的
        Context context = new Context(appName);

        File appDirectory = new File(webapps, appName);
        File classesDirectory = new File(appDirectory, "classes");

        List<File> files = getAllFilePath(classesDirectory);

        for (File clazz : files) {

            String name = clazz.getPath();
            name = name.replace(classesDirectory.getPath() + "\\", "");
            name = name.replace(".class", "");
            name = name.replace("\\", ".");

            try {
                WebappClassLoader classLoader = new WebappClassLoader(new URL[]{classesDirectory.toURL()});
                //加载class文件为Class对象
                Class<?> servletClass = classLoader.loadClass(name);
                if (HttpServlet.class.isAssignableFrom(servletClass)) {
                    //判断这个类是否是一个servlet
                    if (servletClass.isAnnotationPresent(WebServlet.class)) {
                        WebServlet annotation = servletClass.getAnnotation(WebServlet.class);
                        String[] urlPatterns = annotation.urlPatterns();

                        for (String urlPattern : urlPatterns) {
                            //把这个servlet所支持的url和对应的对象保存起来后续处理请求使用！
                            //而这个对象就是使用反射创建的！！！
                            context.addUrlPatternMapping(urlPattern, (Servlet) servletClass.newInstance());
                        }
                    }
                }
            } catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        }

        contextMap.put(appName, context);
    }


    public List<File> getAllFilePath(File srcFile) {
        List<File> result = new ArrayList<>();
        File[] files = srcFile.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getAllFilePath(file));
                } else {
                    result.add(file);
                }
            }
        }

        return result;

    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }
}
