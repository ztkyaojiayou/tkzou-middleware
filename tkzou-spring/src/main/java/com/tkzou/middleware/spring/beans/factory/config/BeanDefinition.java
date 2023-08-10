package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.PropertyValues;
import org.apache.commons.lang3.ObjectUtils;

/**
 * BeanDefinition实例保存bean的信息，包括class类型、方法构造参数、是否为单例等，
 * 此处简化，只包含class类型和bean属性
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 13:38
 */
public class BeanDefinition {
    /**
     * bean的class对象
     */
    private Class beanClass;

    /**
     * bean的属性集合
     */
    private PropertyValues propertyValues;

    /**
     * 默认构造器（这不需要，我们就使用有参构造器即可）
     * 注意：当自定义了构造器之后，该无参默认构造器需要显示声明才可以继续使用
     */
    public BeanDefinition() {
    }

    /**
     * 构造器
     *
     * @param beanClass
     */
    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 全参构造器
     *
     * @param beanClass
     * @param propertyValues
     */
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        //加个判空处理
        this.propertyValues = ObjectUtils.isNotEmpty(propertyValues) ? propertyValues : new PropertyValues();
    }

    /**
     * get-set方法
     *
     * @return
     */
    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
}
