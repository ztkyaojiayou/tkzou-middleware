package com.tkzou.middleware.springframework.beans.factory.support;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.FactoryBean;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.springframework.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import com.tkzou.middleware.springframework.util.StringValueResolver;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bean抽象工厂
 * 作用：
 * 1.具备获取bean对象
 * 1.1对于一般bean的获取，实现BeanFactory
 * 1.2对于单例bean的获取，继承DefaultSingletonBeanRegistry（它会实现SingletonBeanRegistry接口）
 * 2.同时需要注册bean对应的BeanDefinition，由于是抽象接口，因此这里先作定义
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:29
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    /**
     * 增加后置处理器属性，用于在bean的初始化前后进行拓展
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    /**
     * 用于保存factoryBean，也可以理解为存储factoryBean这种bean的容器
     */
    private final Map<String, Object> factoryBeanObjectCache = new HashMap<>();
    /**
     * 用于解析占位符的处理器
     * 通常就一个，这里为PlaceholderResolvingStringValueResolver
     */
    private final List<StringValueResolver> embeddedValueResolvers = new ArrayList<>();
    /**
     * 类型转换器服务
     */
    private ConversionService conversionService;

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        //又是链式调用
        //todo 待完善，因为这里requiredType参数都丢了。。。
        // 且在源码中，该方法的逻辑挺复杂的！
        return ((T) this.getBean(name));
    }

    /**
     * 这里获取单例bean对象
     * 逻辑：
     * 先从容器中获取，若没有则先将该bean注册进容器同时返回
     * 说明：使用了单例模式呀！！！
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object getBean(String beanName) throws BeansException {
        //1.先直接去bean工厂/容器中获取该bean对象
        //这里我们默认获取的是单例对象
        Object sharedInstance = getSingleton(beanName);
        if (ObjectUtils.isNotEmpty(sharedInstance)) {
            //此时可能是FactoryBean，也可能是普通的bean，对于前者，单独处理
            return getObjectForBeanInstance(sharedInstance, beanName);
        }

        //2.若没有，则通过反射生成对象，同时注册进容器
        BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
        //创建这个bean！！！
        Object bean = createBean(beanName, beanDefinition);
        //也需要判断是否是FactoryBean
        return getObjectForBeanInstance(bean, beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return containsBeanDefinition(name);
    }

    protected abstract boolean containsBeanDefinition(String beanName);

    /**
     * 从创建的bean中获取真正的bean
     * 主要是有可能是FactoryBean这种bean，
     * 此时就需要从FactoryBean#getObject中获取/创建bean
     *
     * @param beanInstance 原始bean，也即刚创建出来的bean，这个bean不一定是我们想要的！
     * @param beanName
     * @return
     */
    protected Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        //目标bean
        Object object = beanInstance;
        //1.对于FactoryBean
        if (beanInstance instanceof FactoryBean) {
            FactoryBean factoryBean = (FactoryBean) beanInstance;
            try {
                //1.1若为单例bean
                if (factoryBean.isSingleton()) {
                    //1.1.1singleton作用域bean，先从缓存中获取
                    object = this.factoryBeanObjectCache.get(beanName);
                    if (object == null) {
                        //1.1.2若没有，则再通过getObject方法创建！
                        object = factoryBean.getObject();
                        //再加入缓存
                        this.factoryBeanObjectCache.put(beanName, object);
                    }
                } else {
                    //1.2prototype作用域bean，在直接新建一个bean，且不缓存！
                    object = factoryBean.getObject();
                }
            } catch (Exception ex) {
                throw new BeansException("FactoryBean threw exception on object[" + beanName + "] creation", ex);
            }
        }

        return object;
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    /**
     * 根据beanName和BeanDefinition生成对象
     * 也就是根据class对象和反射生成对象实例
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据bean名称获取BeanDefinition，也即获取对应的class对象
     * 用于根据反射生成bean对象
     *
     * @param beanName
     * @return
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName);

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        //若已有该后置处理器，则先删除
        if (beanPostProcessors.contains(beanPostProcessor)) {
            this.beanPostProcessors.remove(beanPostProcessor);
        }
        //再注册/添加
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        String result = value;
        //遍历所有解析器，解析value
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
        }
        return result;
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
}
