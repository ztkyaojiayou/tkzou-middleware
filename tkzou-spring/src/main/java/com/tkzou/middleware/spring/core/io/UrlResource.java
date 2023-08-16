package com.tkzou.middleware.spring.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 对java.net.URL的输入流类（很少用）
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 19:03
 */
public class UrlResource implements Resource{
    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            throw ex;
        }
    }
}
