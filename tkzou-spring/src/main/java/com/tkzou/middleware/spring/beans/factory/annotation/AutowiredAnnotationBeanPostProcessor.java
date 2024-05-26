package com.tkzou.middleware.spring.beans.factory.annotation;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
        //1.处理字段上的@Value注解
        Class<?> aClass = bean.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        //遍历处理每一个字段，寻找带有@Value注解的字段
        for (Field field : declaredFields) {
            Value annotation = field.getAnnotation(Value.class);
            if (ObjectUtil.isNotEmpty(annotation)) {
                //此时为：${tkzou.name}"，需要解析对应的值
                String value = annotation.value();
                //解析
                value = beanFactory.resolveEmbeddedValue(value);
                //再赋值到这个bean中，完成依赖/属性注入！！！
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }

        //2.处理字段上的@Autowired注解
        //同上，遍历处理每一个字段，寻找带有@Autowired注解的字段
        for (Field field : declaredFields) {
            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (autowiredAnnotation != null) {
                //bean所属类型
                Class<?> fieldType = field.getType();
                String dependentBeanName = null;
                //再看一下是否有@Qualifier注解
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                Object dependentBean = null;
                if (qualifierAnnotation != null) {
                    //属性名称，通过@Qualifier注解定义
                    dependentBeanName = qualifierAnnotation.value();
                    //1.优先按照该beanName去ioc容器中找bean，当前前提是bean属于该type！
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    //2.否则就先按type再按字段名找一个即可
                    //字段名称
                    dependentBeanName = field.getName();
                    //先试图通过字段名称+type查找
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                    if (dependentBean == null) {
                        //若还没有，就再只按type找，此时若只有一个，则返回即可，
                        // 但也可能有多个，且名称都和字段名不同，此时会报错！
                        dependentBean = beanFactory.getBean(fieldType);
                    }
                }
                //再赋值到这个bean中，完成依赖/属性注入！！！
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }

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

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

}
