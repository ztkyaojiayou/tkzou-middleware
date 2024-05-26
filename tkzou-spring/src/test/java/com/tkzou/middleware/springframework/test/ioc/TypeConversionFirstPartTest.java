package com.tkzou.middleware.springframework.test.ioc;

import com.tkzou.middleware.springframework.core.convert.converter.Converter;
import com.tkzou.middleware.springframework.core.convert.support.GenericConversionService;
import com.tkzou.middleware.springframework.core.convert.support.StringToNumberConverterFactory;
import com.tkzou.middleware.springframework.test.common.StringToBooleanConverter;
import com.tkzou.middleware.springframework.test.common.StringToIntegerConverter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 类型转换器测试
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/24 19:01
 */
public class TypeConversionFirstPartTest {

    @Test
    public void testStringToIntegerConverter() throws Exception {
        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer num = converter.convert("8888");
        assertThat(num).isEqualTo(8888);
    }

    @Test
    public void testStringToNumberConverterFactory() throws Exception {
        StringToNumberConverterFactory converterFactory = new StringToNumberConverterFactory();

        Converter<String, Integer> stringToIntegerConverter = converterFactory.getConverter(Integer.class);
        Integer intNum = stringToIntegerConverter.convert("8888");
        assertThat(intNum).isEqualTo(8888);

        Converter<String, Long> stringToLongConverter = converterFactory.getConverter(Long.class);
        Long longNum = stringToLongConverter.convert("8888");
        assertThat(longNum).isEqualTo(8888L);
    }

    @Test
    public void testGenericConverter() throws Exception {
        StringToBooleanConverter converter = new StringToBooleanConverter();

        Boolean flag = (Boolean) converter.convert("true", String.class, Boolean.class);
        assertThat(flag).isTrue();
    }

    /**
     * 核心测试
     *
     * @throws Exception
     */
    @Test
    public void testGenericConversionService() throws Exception {
        GenericConversionService conversionService = new GenericConversionService();
        //先需要自己添加类型转换器
        conversionService.addConverter(new StringToIntegerConverter());
        //然后就可以使用啦！
        Integer intNum = conversionService.convert("8888", Integer.class);
        assertThat(conversionService.canConvert(String.class, Integer.class)).isTrue();
        assertThat(intNum).isEqualTo(8888);
        //同理
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        assertThat(conversionService.canConvert(String.class, Long.class)).isTrue();
        Long longNum = conversionService.convert("8888", Long.class);
        assertThat(longNum).isEqualTo(8888L);
        //同理
        conversionService.addConverter(new StringToBooleanConverter());
        assertThat(conversionService.canConvert(String.class, Boolean.class)).isTrue();
        Boolean flag = conversionService.convert("true", Boolean.class);
        assertThat(flag).isTrue();
    }
}