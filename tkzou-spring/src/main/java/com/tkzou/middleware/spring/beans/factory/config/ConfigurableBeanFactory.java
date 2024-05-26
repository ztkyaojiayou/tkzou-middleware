package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.factory.HierarchicalBeanFactory;
import com.tkzou.middleware.spring.util.StringValueResolver;

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
     * @param value
     * @return
     */
    String resolveEmbeddedValue(String value);

}
