package com.tkzou.middleware.springframework.core.convert.converter;

/**
 * 类型转换器
 * 使用泛型定义，S转T，如String转Integer
 *
 * @author zoutongkun
 */
public interface Converter<S, T> {
    /**
     * 将源对象转换为目标类型
     *
     * @param source
     * @return
     */
    T convert(S source);
}
