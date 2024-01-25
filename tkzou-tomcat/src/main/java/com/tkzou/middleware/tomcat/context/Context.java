package com.tkzou.middleware.tomcat.context;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用类，一个springMVC就是一个应用，它的所有controller都属于一个应用，可以理解为能处理的所有的请求的集合！
 * 平时我们在部署自己的服务时，需要把服务的文件塞到tomcat的webapps这个目录下，
 * tomcat会解析这些文件，并提取出应用名称和对应的用于处理具体请求的servlet使用这个context类封装和保存起来，
 * 后续处理请求时就在这里获取servlet!
 * <p>
 * 详解：
 * 一个Tomcat Server内部可以有多个Service（服务），通常是一个Service。Service内部包含两个组件：
 * Connectors：代表一组Connector（连接器），至少定义一个Connector，也允许定义多个Connector，例如，HTTP和HTTPS两个Connector；
 * Engine：代表一个引擎，所有HTTP请求经过Connector后传递给Engine。
 * 在一个Engine内部，可以有一个或多个Host（主机），Host可以根据域名区分，
 * 在Host内部，又可以有一个或多个Context（上下文），每个Context对应一个Web App。
 * Context是由路径前缀区分的，如/abc、/xyz、/分别代表3个Web App，/表示的Web App在Tomcat中表示根Web App。
 * 因此，对于一个HTTP请求：
 * http://www.example.com/abc/hello
 * tomcat首先会根据域名www.example.com定位到某个Host，
 * 然后根据路径前缀/abc定位到某个Context，
 * 若路径前缀没有匹配到任何Context，则匹配/Context。
 * 在Context内部，就是开发者编写的Web App，一个Context仅包含一个Web App。
 * 可见Tomcat Server是一个全功能的Web服务器，它支持HTTP、HTTPS和AJP等多种Connector，
 * 又能同时运行多个Host，每个Host内部，还可以挂载一个或多个Context，对应一个或多个Web App。
 *
 * @author zoutongkun
 */
public class Context {
    /**
     * 应用名，比如一个springMVC就是一个应用/网站，而这个名称就是在web.xml中的<servlet-name>标签配置的，至此一目了然！！！
     */
    private String appName;
    /**
     * url规则和对应的servlet对象的映射关系，servlet对象就是用于实际处理url请求，具体就是它的service方法！
     */
    private Map<String, Servlet> urlPatternMapping = new HashMap<>();

    /**
     * 构造器
     *
     * @param appName
     */
    public Context(String appName) {
        this.appName = appName;
    }

    /**
     * 添加映射
     *
     * @param urlPattern
     * @param servlet
     */
    public void addUrlPatternMapping(String urlPattern, Servlet servlet) {
        urlPatternMapping.put(urlPattern, servlet);
    }

    /**
     * 获取url对应的servlet对象
     *
     * @param urlPattern
     * @return
     */
    public Servlet getByUrlPattern(String urlPattern) {
        for (String key : urlPatternMapping.keySet()) {
            if (key.contains(urlPattern)) {
                return urlPatternMapping.get(key);
            }
        }
        return null;
    }
}
