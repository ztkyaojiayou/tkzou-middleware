package com.tkzou.middleware.configcenter.client.core;

import com.tkzou.middleware.configcenter.client.domain.ConfigFile;
import org.springframework.web.client.RestTemplate;

/**
 * 配置中心client，与配置中心server端进行交互
 * 本质上就是最朴素的http请求！！！
 *
 * @author zoutongkun
 * @date 2022/9/30 00:16
 */
public class ConfigRpcClient {

    //即http请求调用工具类
    private static final RestTemplate restTemplate = new RestTemplate();

    private final String serverAddr;

    public ConfigRpcClient(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    /**
     * 获取指定配置文件的最新数据
     * 本质就是http请求
     *
     * @param fileId
     * @return
     */
    public ConfigFile getConfig(String fileId) {
        //拼接url
        return restTemplate.getForObject("http://" + serverAddr + "/v1/config/" + fileId, ConfigFile.class);
    }

}
