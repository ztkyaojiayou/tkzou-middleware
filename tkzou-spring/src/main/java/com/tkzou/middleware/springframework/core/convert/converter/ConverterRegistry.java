package com.tkzou.middleware.springframework.core.convert.converter;

/**
 * 类型转换器注册接口
 * 即用于管理类型转换器
 *
 * @author zoutongkun
 */
public interface ConverterRegistry {
    /**
     * 添加一个Converter
     *
     * @param converter
     */
    void addConverter(Converter<?, ?> converter);

    /**
     * 添加一个ConverterFactory
     *
     * @param converterFactory
     */
    void addConverterFactory(ConverterFactory<?, ?> converterFactory);

    /**
     * 添加一个GenericConverter
     *
     * @param genericConverter
     */
    void addConverter(GenericConverter genericConverter);
}
