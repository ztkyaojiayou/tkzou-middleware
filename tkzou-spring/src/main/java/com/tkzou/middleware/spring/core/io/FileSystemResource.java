package com.tkzou.middleware.spring.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * 文件系统的输入流类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 17:10
 */
public class FileSystemResource implements Resource {

    /**
     * 任意的文件路径（绝对路径）
     */
    private final String filePath;

    /**
     * 构造器
     * 有成员变量，就要习惯性地加上构造器
     *
     * @param filePath
     */
    public FileSystemResource(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取当前文件路径的输入流
     *
     * @return
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {

        try {
            Path path = new File(this.filePath).toPath();
            return Files.newInputStream(path);
        } catch (NoSuchFileException e) {
            //把异常转换了一下，但都是IOException的子类
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
