package com.tkzou.middleware.spring.beans.factory.annotation;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.PropertyValues;
import com.tkzou.middleware.spring.beans.factory.BeanFactory;
import com.tkzou.middleware.spring.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.spring.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

/**
 * @author :zoutongkun
 * @date :2024/5/26 10:40 上午
 * @description :
 * @modyified By:
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    /**
     * ioc容器对象，通过实现BeanFactoryAware注入！
     * 在spring中，随时通过实现BeanFactoryAware来注入ioc容器对象
     * 已经是一个常规操作啦！
     */
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * 处理@Value和@Autowired注解
     * 即给当前bean注入这两个注解所对应的属性
     *
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        //1.处理@Value注解
        Class<?> aClass = bean.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            Value annotation = field.getAnnotation(Value.class);
            if (ObjectUtil.isNotEmpty(annotation)) {
                //此时为：${tkzou.name}"，需要解析对应的值
                String value = annotation.value();
                //解析
                value = beanFactory.resolveEmbeddedValue(value);
                //再赋值
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }


        //2.处理@Autowired注解
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

}
