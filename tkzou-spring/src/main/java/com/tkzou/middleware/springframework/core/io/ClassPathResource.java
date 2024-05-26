package com.tkzou.middleware.springframework.core.io;

import org.apache.commons.lang3.ObjectUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * classpath路径下的资源输入流类
 * 也即获取的是当前项目的类路径下的资源
 * 具体而言：编译时从src目录下（易知包括resources目录）开始搜索；运行时从bin目录下开始搜索
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 17:36
 */
public class ClassPathResource implements Resource {

    private final String path;

    /**
     * 构造器
     *
     * @param path
     */
    public ClassPathResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {

        //获取当前项目类路径下指定文件的输入流
        //易知，这个方法中用到了成员变量path，那么易知前提就是先要构造这个对象，易知使用构造器即可！！！
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(this.path);
        if (ObjectUtils.isEmpty(resourceAsStream)) {
            throw new FileNotFoundException("当前路径" + this.path + "无法被打开，请检查是否存在................");
        }
        return resourceAsStream;
    }
}
