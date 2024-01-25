package com.tkzou.middleware.tomcat;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zoutongkun
 */
public class WebappClassLoader extends URLClassLoader {

    public WebappClassLoader(URL[] urls) {
        super(urls);
    }
}
