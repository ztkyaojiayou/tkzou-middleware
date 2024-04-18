package com.tkzou.middleware.configcenter.client.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 配置文件实体类
 * 即对一个配置文件的封装，如xxx.properties
 * @author zoutongkun
 * @date 2022/9/29 23:43
 */
@Getter
@Setter
@Accessors(chain = true)
public class ConfigFile {

    /**
     * 配置文件的唯一id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件后缀名
     */
    private String extension;

    /**
     * 文件的内容
     */
    private String content;

    /**
     * 上一次更新的时间戳
     */
    private Long lastUpdateTimestamp;

}
