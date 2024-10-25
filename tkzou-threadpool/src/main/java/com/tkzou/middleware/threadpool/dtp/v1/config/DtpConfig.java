package com.tkzou.middleware.threadpool.dtp.v1.config;

import com.tkzou.middleware.threadpool.dtp.v1.common.constant.DtpConfigConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 动态线程池配置类
 *
 * @author zoutongkun
 * @Date 2023/5/19 22:57
 */
@Data
@ConfigurationProperties(prefix = DtpConfigConstant.PROPERTIES_PREFIX)
public class DtpConfig {
    /**
     * 是否开启动态线程池监控
     */
    private boolean enableMonitor = false;
    /**
     * 动态线程池监控信息上报频率
     */
    private long collectInterval = 5;
    /**
     * 动态线程池配置参数
     */
    private List<ThreadPoolProperties> executors;

    private NacosConfig nacos;
}
