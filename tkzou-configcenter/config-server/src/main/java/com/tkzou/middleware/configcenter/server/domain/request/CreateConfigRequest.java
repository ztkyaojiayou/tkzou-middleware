package com.tkzou.middleware.configcenter.server.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 *
 * @author zoutongkun
 * @date 2022/9/30 00:12
 */
@Getter
@Setter
@Accessors(chain = true)
public class CreateConfigRequest {

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

}
