package com.tkzou.middleware.springframework.test.ioc;

import cn.hutool.core.io.IoUtil;
import com.tkzou.middleware.springframework.core.io.DefaultResourceLoader;
import com.tkzou.middleware.springframework.core.io.FileSystemResource;
import com.tkzou.middleware.springframework.core.io.Resource;
import com.tkzou.middleware.springframework.core.io.UrlResource;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 资源加载测试类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/16 19:10
 */
public class ResourceAndResourceLoaderTest {

    @Test
    public void testResourceLoader()throws Exception{
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

        //加载classpath下的资源
        Resource resource = resourceLoader.getResource("classpath:hello.txt");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);

        //加载文件系统资源
        resource = resourceLoader.getResource("src/test/resources/hello.txt");
        assertThat(resource instanceof FileSystemResource).isTrue();
        inputStream = resource.getInputStream();
        content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
        assertThat(content).isEqualTo("hello world");

        //加载url资源
        resource = resourceLoader.getResource("https://www.baidu.com");
        assertThat(resource instanceof UrlResource).isTrue();
        inputStream = resource.getInputStream();
        content = IoUtil.readUtf8(inputStream);
        System.out.println(content);

    }
}
