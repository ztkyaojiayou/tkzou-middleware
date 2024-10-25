package com.tkzou.middleware.threadpool.dtp.v1.common.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.util.Map;

/**
 * @author zoutongkun
 * @Date 2023/5/24 19:41
 */
public class ParseUtil {
    public static Map<Object, Object> parseYaml(String content) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ByteArrayResource(content.getBytes()));
        return factoryBean.getObject();
    }
}
