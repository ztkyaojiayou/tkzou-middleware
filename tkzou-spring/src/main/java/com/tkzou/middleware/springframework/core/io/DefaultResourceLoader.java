package com.tkzou.middleware.springframework.core.io;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 资源加载的默认实现类
 * 默认优先加载classpath下的资源
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/11 9:51
 */
public class DefaultResourceLoader implements ResourceLoader {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    @Override
    public Resource getResource(String location) {

        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            //此时加载classpath路径下的资源
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                //尝试当成url处理
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                //再尝试当成系统文件下的资源处理
                return new FileSystemResource(location);
            }
        }
    }
}
