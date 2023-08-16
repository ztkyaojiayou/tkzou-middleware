package com.tkzou.middleware.spring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源的访问接口
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 16:44
 */
public interface Resource {

    /**
     * 获取输入流
     *
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;
}
