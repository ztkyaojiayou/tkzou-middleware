package com.tkzou.middleware.configcenter.server.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author zoutongkun
 * @date 2022/9/30 00:12
 */
@Getter
@Setter
@Accessors(chain = true)
public class UpdateConfigRequest extends CreateConfigRequest {

    /**
     * 文件id
     */
    private String fileId;

}
