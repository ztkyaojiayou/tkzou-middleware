package com.tkzou.middleware.threadpool.dtp.v1.core.refresh;


import cn.hutool.core.util.ArrayUtil;
import com.tkzou.middleware.threadpool.dtp.v1.common.utils.ParseUtil;
import com.tkzou.middleware.threadpool.dtp.v1.common.utils.ResourceBundlerUtil;
import com.tkzou.middleware.threadpool.dtp.v1.config.DtpConfig;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import com.tkzou.middleware.threadpool.dtp.v1.core.core.DtpRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zoutongkun
 * @Date 2023/5/21 14:44
 */
@Slf4j
public class AbstractRefresher implements Refresher {
    @Resource
    protected DtpConfig dtpConfig;

    @Override
    public void refresh(String configInfo) {
        //解析yml
        ResourceBundlerUtil.bind(ParseUtil.parseYaml(configInfo), dtpConfig);
        List<ThreadPoolProperties> executors = dtpConfig.getExecutors();
        if (!ArrayUtil.isEmpty(executors)) {
            executors.forEach((executor) -> DtpRegistry.refresh(executor.getPoolName(), executor));
        }
    }
}
