
package com.tkzou.middleware.springcloud.registercenter.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务实例
 *
 * @author zoutongkun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo {

    private String ip;

    private Integer port;
}