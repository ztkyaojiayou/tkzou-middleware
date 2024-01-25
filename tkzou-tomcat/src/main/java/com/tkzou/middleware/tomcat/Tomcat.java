package com.tkzou.middleware.tomcat;

import cn.hutool.core.thread.ThreadUtil;

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
 * @author zoutongkun
 */
public class Tomcat {

    private Map<String, Context> contextMap = new HashMap<>();

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

    /**
     * 启动类
     *
     * @param args
     */
    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.deployApps();
        tomcat.start();
    }

    private void deployApps() {
        //获取所有的系统属性
        Properties properties = System.getProperties();
        String curPath = System.getProperty("user.dir") + "/tkzou-tomcat";
        File webapps = new File(curPath, "webapps");
        for (String app : Objects.requireNonNull(webapps.list())) {
            deployApp(webapps, app);
        }
    }

    private void deployApp(File webapps, String appName) {
        // 有哪些Servlet

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
                Class<?> servletClass = classLoader.loadClass(name);
                if (HttpServlet.class.isAssignableFrom(servletClass)) {
                    if (servletClass.isAnnotationPresent(WebServlet.class)) {
                        WebServlet annotation = servletClass.getAnnotation(WebServlet.class);
                        String[] urlPatterns = annotation.urlPatterns();

                        for (String urlPattern : urlPatterns) {
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
