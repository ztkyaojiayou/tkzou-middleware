package com.tkzou.middleware.springframework.core.convert.support;

import cn.hutool.core.convert.BasicType;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import com.tkzou.middleware.springframework.core.convert.converter.Converter;
import com.tkzou.middleware.springframework.core.convert.converter.ConverterFactory;
import com.tkzou.middleware.springframework.core.convert.converter.ConverterRegistry;
import com.tkzou.middleware.springframework.core.convert.converter.GenericConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 基础实现类
 * 类型转换的核心类
 *
 * @author :zoutongkun
 * @date :2024/5/26 4:20 下午
 * @description :
 * @modyified By:
 */
public class GenericConversionService implements ConversionService, ConverterRegistry {
    /**
     * 保存所有的GenericConverter型的类型转换器
     * 但类型转换器有好几种，因此就涉及到适配的问题了，
     * 也即需要使用适配器模式！
     * key：转换器的源类型和目标类型对
     * value：对应的转换器
     */
    private Map<GenericConverter.ConvertiblePair, GenericConverter> converters = new HashMap<>();

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        //只需看map中是否有这个转换对对应的转换器即可
        //也即是否支持这个转换对的转换
        GenericConverter converter = getConverter(sourceType, targetType);
        //有就支持
        return converter != null;
    }

    /**
     * 获取支持当前转换对的转换器
     *
     * @param sourceType
     * @param targetType
     * @return
     */
    private GenericConverter getConverter(Class<?> sourceType, Class<?> targetType) {
        //考虑到有ConverterFactory这种类型的转换器工厂，它可以用于转换某一个类型的子类，
        //因此我们这里需要先获取到需要转换的源类型和目的类型的所有父类
        //再逐一去map中找是否有支持这一个转换对的转换器
        List<Class<?>> sourceCandidates = getClassHierarchy(sourceType);
        List<Class<?>> targetCandidates = getClassHierarchy(targetType);
        //再使用两个循环来组合转换对
        for (Class<?> sourceCandidate : sourceCandidates) {
            for (Class<?> targetCandidate : targetCandidates) {
                //封装为ConvertiblePair，也即map中的key
                GenericConverter.ConvertiblePair convertiblePair =
                        new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                //再去map中查找是否有支持这个转换对的类型转换器
                //找到了就返回
                GenericConverter converter = converters.get(convertiblePair);
                if (converter != null) {
                    return converter;
                }
            }
        }

        return null;
    }

    /**
     * 获取所有的父类class
     *
     * @param clazz
     * @return
     */
    private List<Class<?>> getClassHierarchy(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        //兼容一下基本类型
        clazz = BasicType.wrap(clazz);
        //循环获取该类的所有父类
        while (clazz != null) {
            hierarchy.add(clazz);
            //父类
            clazz = clazz.getSuperclass();
        }
        return hierarchy;
    }

    /**
     * 类型转换
     *
     * @param source     源对象，那么就有源类型
     * @param targetType 目标类型
     * @param <T>
     * @return
     */
    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        //源类型
        Class<?> sourceType = source.getClass();
        //兼容一下基本类型
        targetType = (Class<T>) BasicType.wrap(targetType);
        boolean canConvert = canConvert(sourceType, targetType);
        if (canConvert) {
            //获取支持当前转换对的转换器
            GenericConverter converter = getConverter(sourceType, targetType);
            //再转换
            return (T) converter.convert(source, sourceType, targetType);
        }

        throw new RuntimeException("不支持当前转换对的转换");
    }

    /**
     * 先添加Converter/类型转换器
     *
     * @param converter
     */
    @Override
    public void addConverter(Converter<?, ?> converter) {
        //解析类型转换器上的泛型参数信息
        GenericConverter.ConvertiblePair typeInfo = getRequiredTypeInfo(converter);
        //使用适配器模式，即通过ConverterAdapter适配Converter
        ConverterAdapter converterAdapter = new ConverterAdapter(typeInfo, converter);
        //添加到map中备用
        for (GenericConverter.ConvertiblePair convertibleType : converterAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterAdapter);
        }

    }

    /**
     * 解析类型转换器对象所属类的泛型参数信息
     * 得到它的源转换类型和目的转换类型
     *
     * @param converter
     * @return
     */
    private GenericConverter.ConvertiblePair getRequiredTypeInfo(Object converter) {
        Type[] types = converter.getClass().getGenericInterfaces();
        ParameterizedType parameterized = (ParameterizedType) types[0];
        Type[] actualTypeArguments = parameterized.getActualTypeArguments();
        Class sourceType = (Class) actualTypeArguments[0];
        Class targetType = (Class) actualTypeArguments[1];
        //组装成ConvertiblePair，也即map中的key
        return new GenericConverter.ConvertiblePair(sourceType, targetType);
    }

    /**
     * 添加ConverterFactory/类型转换器工厂
     *
     * @param converterFactory
     */
    @Override
    public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
        GenericConverter.ConvertiblePair requiredTypeInfo = getRequiredTypeInfo(converterFactory);
        //适配/包装/转换一下
        ConverterFactoryAdapter converterFactoryAdapter = new ConverterFactoryAdapter(requiredTypeInfo,
                converterFactory);
        //按照可转换的转换对依次保存对应的转换器
        for (GenericConverter.ConvertiblePair convertibleType : converterFactoryAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterFactoryAdapter);
        }
    }

    /**
     * 同理，只是添加的直接就是GenericConverter
     *
     * @param converter
     */
    @Override
    public void addConverter(GenericConverter converter) {
        for (GenericConverter.ConvertiblePair convertibleType : converter.getConvertibleTypes()) {
            converters.put(convertibleType, converter);
        }
    }

    /**
     * 适配Converter类型转换器的GenericConverter转换器
     * 使用了适配器模式
     */
    private final class ConverterAdapter implements GenericConverter {
        /**
         * 支持转换的源类型和目的类型对
         */
        private final ConvertiblePair typeInfo;
        /**
         * 对应的类型转换器
         */
        private final Converter<Object, Object> converter;

        public ConverterAdapter(ConvertiblePair typeInfo, Converter<?, ?> converter) {
            this.typeInfo = typeInfo;
            this.converter = (Converter<Object, Object>) converter;
        }

        /**
         * 获取所有可支持转换的类型对
         *
         * @return
         */
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(typeInfo);
        }

        /**
         * 类型转换
         *
         * @param source     目标对象
         * @param sourceType 源类型
         * @param targetType 目标类型
         * @return
         */
        @Override
        public Object convert(Object source, Class sourceType, Class targetType) {
            return converter.convert(source);
        }
    }


    /**
     * 适配ConverterFactory类型转换器的GenericConverter转换器
     * 也是使用了适配器模式
     */
    private final class ConverterFactoryAdapter implements GenericConverter {

        private final ConvertiblePair typeInfo;

        private final ConverterFactory<Object, Object> converterFactory;

        public ConverterFactoryAdapter(ConvertiblePair typeInfo, ConverterFactory<?, ?> converterFactory) {
            this.typeInfo = typeInfo;
            this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(typeInfo);
        }

        @Override
        public Object convert(Object source, Class sourceType, Class targetType) {
            //从这个转换器工厂中根据传入的类型选一个具体的类型转换器来进行转换！
            //最终还是Converter在进行转换！
            return converterFactory.getConverter(targetType).convert(source);
        }
    }

}
