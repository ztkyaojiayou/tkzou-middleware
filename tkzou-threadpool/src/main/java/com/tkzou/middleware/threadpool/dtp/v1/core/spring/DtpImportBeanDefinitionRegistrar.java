package com.tkzou.middleware.threadpool.dtp.v1.core.spring;

import com.tkzou.middleware.threadpool.dtp.v1.common.utils.BeanUtil;
import com.tkzou.middleware.threadpool.dtp.v1.common.utils.ResourceBundlerUtil;
import com.tkzou.middleware.threadpool.dtp.v1.config.DtpConfig;
import com.tkzou.middleware.threadpool.dtp.v1.config.ThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Objects;

/**
 * @author zoutongkun
 * @Date 2023/5/19 23:52
 */
@Slf4j
public class DtpImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info("注册");
        //绑定资源
        DtpConfig dtpConfig = new DtpConfig();
        ResourceBundlerUtil.bind(environment, dtpConfig);
        List<ThreadPoolProperties> executors = dtpConfig.getExecutors();
        if (Objects.isNull(executors)) {
            log.info("未检测本地到配置文件线程池");
            return;
        }
        //注册beanDefinition
        executors.forEach((executorProp) -> {
            BeanUtil.registerIfAbsent(registry, executorProp);
        });
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
