package com.tkzou.middleware.threadpool.dtp.v1.core.refresh.impl;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.tkzou.middleware.threadpool.dtp.v1.config.NacosConfig;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpThreadPoolCreator;
import com.tkzou.middleware.threadpool.dtp.v1.core.refresh.AbstractRefresher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zoutongkun
 * @Date 2023/5/21 15:01
 */
@Slf4j
public class NacosRefresher extends AbstractRefresher implements Listener, InitializingBean, DisposableBean {
    private static final ThreadPoolExecutor EXECUTOR = DtpThreadPoolCreator.createNormalExecutor("Nacos-tp");

    @NacosInjected
    private ConfigService configService;

    @Override
    public void afterPropertiesSet() throws Exception {
        NacosConfig nacos = dtpConfig.getNacos();
        if (nacos != null) {
            configService.addListener(nacos.getDataId(), nacos.getGroup(), this);
            return;
        }
        log.info("Nacos未配置");
    }

    @Override
    public Executor getExecutor() {
        return EXECUTOR;
    }

    /**
     * 接收nacos中的动态配置！！！
     *
     * @param newConfigInfo
     */
    @Override
    public void receiveConfigInfo(String newConfigInfo) {
        List<ThreadPoolProperties> executors = dtpConfig.getExecutors();
        if (executors != null) {
            refresh(newConfigInfo);
            return;
        }
        log.info("配置为空");
    }

    @Override
    public void destroy() {
        EXECUTOR.shutdown();
    }
}
