package com.tkzou.middleware.springframework.core.convert;

/**
 * 类型转换接口
 * 专门干活的
 *
 * @author zoutongkun
 */
public interface ConversionService {
    /**
     * 判断是否支持将源类型转换为目标类型
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    /**
     * 将源对象转换为目标类型
     *
     * @param source     源对象，那么就有源类型
     * @param targetType 目标类型
     * @param <T>
     * @return
     */
    <T> T convert(Object source, Class<T> targetType);
}
