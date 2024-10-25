package com.tkzou.middleware.threadpool.dtp.v1.core.spring;

import cn.hutool.core.util.ArrayUtil;
import com.tkzou.middleware.threadpool.dtp.v1.config.DtpConfig;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zoutongkun
 * @Date 2023/5/23 0:47
 */
@Slf4j
public class DtpLifeCycle implements SmartLifecycle {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    @Resource
    private DtpConfig dtpConfig;

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            log.info("lifecycle start");
            //同步远端配置
            List<ThreadPoolProperties> executors = dtpConfig.getExecutors();
            if (!ArrayUtil.isEmpty(executors)) {
                for (ThreadPoolProperties executor : executors) {
                    DtpRegistry.refresh(executor.getPoolName(), executor);
                }
            }
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            log.info("lifecycle stop");
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
