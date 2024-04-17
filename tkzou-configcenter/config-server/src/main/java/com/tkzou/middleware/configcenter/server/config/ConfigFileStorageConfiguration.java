package com.tkzou.middleware.configcenter.server.config;

import com.tkzou.middleware.configcenter.server.config.ConfigServerConfig;
import com.tkzou.middleware.configcenter.server.mapper.ConfigMapper;
import com.tkzou.middleware.configcenter.server.mapper.FileSystemConfigMapper;
import com.tkzou.middleware.configcenter.server.mapper.InMemoryConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 存储介质配置
 *
 * @author zoutongkun
 */
@Configuration
public class ConfigFileStorageConfiguration {

    public static final String FILE = "file";
    public static final String MEMORY = "memory";

    @Autowired
    private ConfigServerConfig configServerConfig;

    /**
     * 配置一种存储介质
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigMapper configFileStorage() {
        String storeType = configServerConfig.getStoreType().toLowerCase();
        if (FILE.equals(storeType)) {
            return new FileSystemConfigMapper();
        }
        if (MEMORY.equals(storeType)) {
            return new InMemoryConfigMapper();
        }

        throw new RuntimeException("storeType=" + storeType + "无对应的ConfigMapper实现");
    }

}
