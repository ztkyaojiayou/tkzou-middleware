package com.tkzou.middleware.tomcat;

/**
 * 主启动类
 *
 * @author zoutongkun
 */
public class MyTomcatBootStrap {
    /**
     * 启动项目！！！
     *
     * @param args
     */
    public static void main(String[] args) {
        MyTomcat myTomcat = new MyTomcat();
        //部署服务
        myTomcat.deployApps();
        //启动tomcat
        myTomcat.start();
    }
}
