package com.tkzou.middleware.springframework.core.convert.converter;

/**
 * 转换器工厂，用于创建转换器
 * 该接口用于创建一个转换器，
 * 且获取的是S转R的任意子类的转换器，
 * 如String转Number下的各种子类，如Integer，Float等
 *
 * @author zoutongkun
 */
public interface ConverterFactory<S, R> {
    /**
     * 获取一个转换器，该转换器可以将一个对象从类型 S 转换为类型 R的子类型。
     *
     * @param targetType 目标类型的class对象
     * @param <T>        R的子类型
     * @return
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
