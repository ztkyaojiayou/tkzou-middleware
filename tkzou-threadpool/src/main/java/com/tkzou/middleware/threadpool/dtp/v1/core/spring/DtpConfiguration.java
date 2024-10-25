package com.tkzou.middleware.threadpool.dtp.v1.core.spring;

import com.tkzou.middleware.threadpool.dtp.v1.config.DtpConfig;
import com.tkzou.middleware.threadpool.dtp.v1.core.refresh.impl.NacosRefresher;
import com.tkzou.middleware.threadpool.dtp.v1.monitor.DtpMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zoutongkun
 * @Date 2023/5/20 17:48
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(DtpConfig.class)
public class DtpConfiguration {
    @Bean
    public NacosRefresher nacosRefresher() {
        return new NacosRefresher();
    }

    @Bean
    public DtpMonitor dtpMonitor() {
        return new DtpMonitor();
    }
}
