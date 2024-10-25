package com.tkzou.middleware.threadpool.dtp.v1.common.utils;

import com.tkzou.middleware.threadpool.dtp.v1.common.constant.DtpConfigConstant;
import com.tkzou.middleware.threadpool.dtp.v1.config.DtpConfig;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author zoutongkun
 * @Date 2023/5/20 14:40
 */
public class ResourceBundlerUtil {
    public static void bind(Environment environment, DtpConfig dtpConfig) {
        Binder binder = Binder.get(environment);
        ResolvableType resolvableType = ResolvableType.forClass(DtpConfig.class);
        Bindable<Object> bindable = Bindable.of(resolvableType).withExistingValue(dtpConfig);
        binder.bind(DtpConfigConstant.PROPERTIES_PREFIX, bindable);
    }

    public static void bind(Map<?, Object> properties, DtpConfig dtpConfig) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(sources);
        ResolvableType type = ResolvableType.forClass(DtpConfig.class);
        Bindable<?> target = Bindable.of(type).withExistingValue(dtpConfig);
        binder.bind(DtpConfigConstant.PROPERTIES_PREFIX, target);
    }
}
