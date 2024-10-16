package com.tkzou.middleware.springframework.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.TypeUtil;
import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValues;
import com.tkzou.middleware.springframework.beans.factory.BeanFactory;
import com.tkzou.middleware.springframework.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.tkzou.middleware.springframework.core.convert.ConversionService;

import java.lang.reflect.Field;

/**
 * @author :zoutongkun
 * @date :2024/5/26 10:40 上午
 * @description :
 * @modyified By:
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor,
    BeanFactoryAware {
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
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean,
                                                    String beanName) throws BeansException {
        //1.处理字段上的@Value注解
        Class<?> aClass = bean.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        //遍历处理每一个字段，寻找带有@Value注解的字段
        for (Field field : declaredFields) {
            //判断是否带有@Value注解
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (ObjectUtil.isNotEmpty(valueAnnotation)) {
                //此时为：${tkzou.name}"，需要解析对应的值
                Object value = valueAnnotation.value();
                //解析
                value = beanFactory.resolveEmbeddedValue((String) value);

                //因为加了类型转换器，因此，对于非引用类型的字段，需要考虑类型转换
                //即根据用户配置的类型转换器来判断是否需要转换类型！
                Class<?> sourceType = value.getClass();
                Class<?> targetType = (Class<?>) TypeUtil.getType(field);
                //如果有类型器，则使用该转换器进行转换
                ConversionService conversionService = beanFactory.getConversionService();
                if (conversionService != null) {
                    if (conversionService.canConvert(sourceType, targetType)) {
                        value = conversionService.convert(value, targetType);
                    }
                }

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
                    //若没有就顺势创建！！！
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    //2.否则就先按type再按字段名找一个即可
                    //字段名称
                    dependentBeanName = field.getName();
                    //先试图通过字段名称+type查找
                    //若没有就顺势创建！！！
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                    if (dependentBean == null) {
                        //若还没有，就再只按type找，此时若只有一个，则返回即可，
                        // 但也可能有多个，且名称都和字段名不同，此时会报错！
                        //若没有就顺势创建！！！
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
