package com.tkzou.middleware.springframework.test.common;


import com.tkzou.middleware.springframework.core.convert.converter.Converter;

/**
 * String转Integer
 * @author zoutongkun
 */
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}
