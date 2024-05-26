package com.tkzou.middleware.springframework.core.convert.support;

import com.tkzou.middleware.springframework.core.convert.converter.Converter;
import com.tkzou.middleware.springframework.core.convert.converter.ConverterFactory;

/**
 * 字符串转数字的转换器工厂
 *
 * @author :zoutongkun
 * @date :2024/5/26 4:20 下午
 * @description :
 * @modyified By:
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<T>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            if (source.length() == 0) {
                return null;
            }

            if (targetType.equals(Integer.class)) {
                return (T) Integer.valueOf(source);
            } else if (targetType.equals(Long.class)) {
                return (T) Long.valueOf(source);
            }
            //TODO 其他数字类型

            else {
                throw new IllegalArgumentException(
                        "Cannot convert String [" + source + "] to target class [" + targetType.getName() + "]");
            }
        }
    }

}