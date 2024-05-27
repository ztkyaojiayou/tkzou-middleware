package com.tkzou.middleware.springframework.context.support;

import cn.hutool.core.collection.CollectionUtil;
import com.tkzou.middleware.springframework.beans.factory.FactoryBean;
import com.tkzou.middleware.springframework.beans.factory.InitializingBean;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import com.tkzou.middleware.springframework.core.convert.converter.Converter;
import com.tkzou.middleware.springframework.core.convert.converter.ConverterFactory;
import com.tkzou.middleware.springframework.core.convert.converter.ConverterRegistry;
import com.tkzou.middleware.springframework.core.convert.converter.GenericConverter;
import com.tkzou.middleware.springframework.core.convert.support.DefaultConversionService;
import com.tkzou.middleware.springframework.core.convert.support.GenericConversionService;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 用于初始化ConversionService
 * 作用于全局，也即所有bean中的非引用类型的属性都要通过该转换服务进行转换！
 * 要注意的是，只有非引用类型的字段才需要考虑类型转换！
 * 需要类型转换的时机有两处：
 * 1.为bean填充非引用类型的属性时，见AbstractAutowireCapableBeanFactory#applyPropertyValues
 * 2.处理@Value注解时，它所修饰的也都是非引用类型的字段，见AutowiredAnnotationBeanPostProcessor#postProcessPropertyValues
 *
 * @author zoutongkun
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {
    /**
     * 转换器集合
     * todo 什么时候初始化呢？目前是在配置文件中作为一个属性配置的，
     *   也即会在AbstractAutowireCapableBeanFactory#applyPropertyValues方法中作为引用类型的属性进行注入！！！
     */
    private Set<?> converters;
    /**
     * 转换器服务对象
     * 核心
     */
    private GenericConversionService conversionService;

    @Override
    public ConversionService getObject() throws Exception {
        return conversionService;
    }

    @Override
    public Class<?> getObjectType() {
        return conversionService.getClass();
    }

    /**
     * bean初始化后会回调该方法
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //new一个用于转换的服务对象
        conversionService = createConversionService();
        //注册具体的转换器
        registerConverters(converters, conversionService);
    }

    /**
     * 创建DefaultConversionService
     * 这里是为了与spring源码的代码结构保持一致，因此单独抽出了一个方法
     *
     * @return
     */
    @NotNull
    private GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }

    /**
     * 注册转换器
     *
     * @param converters
     * @param registry
     */
    private void registerConverters(Set<?> converters, ConverterRegistry registry) {
        if (CollectionUtil.isNotEmpty(converters)) {
            for (Object converter : converters) {
                //区分三类转换器
                if (converter instanceof GenericConverter) {
                    registry.addConverter((GenericConverter) converter);
                } else if (converter instanceof Converter) {
                    registry.addConverter((Converter<?, ?>) converter);
                } else if (converter instanceof ConverterFactory) {
                    registry.addConverterFactory((ConverterFactory<?, ?>) converter);
                } else {
                    //抛异常
                    throw new IllegalArgumentException("Each converter object must implement one of the " +
                            "Converter, ConverterFactory, or GenericConverter interfaces");
                }

            }
        }
    }

    /**
     * 设置自定义的转换器
     * 一般就是在配置文件中设置，
     * 若要使用类型转换器，则这一步必须有，否则就没有转换器使用呀！
     *
     * @param converters
     */
    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
