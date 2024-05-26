package com.tkzou.middleware.springframework.core.convert.support;

import com.tkzou.middleware.springframework.core.convert.converter.ConverterRegistry;

/**
 * 默认的类型转换服务
 * 提供添加一些常用的类型转换器。
 *
 * @author :zoutongkun
 * @date :2024/5/26 4:20 下午
 * @description :
 * @modyified By:
 */
public class DefaultConversionService extends GenericConversionService {

    public DefaultConversionService() {
        addDefaultConverters(this);
    }

    /**
     * 添加自定义的默认的类型转换器
     *
     * @param converterRegistry
     */
    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        //TODO 添加其他ConverterFactory
    }
}
