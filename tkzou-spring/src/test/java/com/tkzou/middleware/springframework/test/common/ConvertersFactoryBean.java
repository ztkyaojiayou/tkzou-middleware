package com.tkzou.middleware.springframework.test.common;

import com.tkzou.middleware.springframework.beans.factory.FactoryBean;

import java.util.HashSet;
import java.util.Set;

/**
 * 添加自定义的类型转换器，可以使用FactoryBean配置，
 * 也可以使用一般的方式配置，如@component或@configuration注解重写这个bean，
 * 再调用ConversionServiceFactoryBean#setConverters方法设置来配置！
 * 否则spring框架无法扫描到，就无法识别它，就不生效！
 * 目前这个bean在xml中配置！
 * 参考：https://blog.csdn.net/Facial_Mask/article/details/134701361
 *
 * @author zoutongkun
 */
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {

    @Override
    public Set<?> getObject() throws Exception {
        HashSet<Object> converters = new HashSet<>();
        //属于基础的类型转换，可以添加其他类型转换器
        StringToLocalDateConverter stringToLocalDateConverter = new StringToLocalDateConverter("yyyy-MM-dd");
        converters.add(stringToLocalDateConverter);
        return converters;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
