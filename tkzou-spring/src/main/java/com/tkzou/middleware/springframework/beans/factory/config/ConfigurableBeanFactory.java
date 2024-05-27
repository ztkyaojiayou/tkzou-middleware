package com.tkzou.middleware.springframework.beans.factory.config;

import com.tkzou.middleware.springframework.beans.factory.HierarchicalBeanFactory;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import com.tkzou.middleware.springframework.util.StringValueResolver;

/**
 * 不知道是干嘛，先写着！
 * 更新：提供添加/扫描/注册BeanPostProcessor
 *
 * @author zoutongkun
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * 添加/扫描/注册BeanPostProcessor
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 销毁所有的单例bean！！！
     * 易知，多例/原型bean不管哦！！！
     */
    void destroySingletons();

    /**
     * 添加StringValueResolver，
     * 用于解析@Value注解中的占位符
     *
     * @param valueResolver
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * 解析占位符，如@Value("${some.property.key}")中的${some.property.key}
     *
     * @param value
     * @return
     */
    String resolveEmbeddedValue(String value);

    /**
     * 设置类型转换器服务
     *
     * @param conversionService
     */
    void setConversionService(ConversionService conversionService);

    /**
     * 获取类型转换器服务
     *
     * @return
     */
    ConversionService getConversionService();

}
