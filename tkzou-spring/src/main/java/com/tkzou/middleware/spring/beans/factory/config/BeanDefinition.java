package com.tkzou.middleware.spring.beans.factory.config;

import com.tkzou.middleware.spring.beans.PropertyValues;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * BeanDefinition实例保存bean的信息，包括class类型、方法构造参数、是否为单例等，
 * 此处简化，只包含class类型和bean属性
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 13:38
 */
public class BeanDefinition {
    public static String SCOPE_SINGLETON = "singleton";

    public static String SCOPE_PROTOTYPE = "prototype";

    /**
     * bean的class对象
     */
    private Class beanClass;

    /**
     * bean的属性集合
     * 初始化一下，防止在调用addPropertyValue方法时出现NPE
     */
    private PropertyValues propertyValues = new PropertyValues();

    /**
     * 新增两个属性：在bean的属性初始化后会被执行的初始化方法的bean名称和在bean销毁前需要执行的销毁方法的bean名称
     */
    private String initMethodName;
    /**
     * 新在bean销毁前需要执行的销毁方法的名称
     */
    private String destroyMethodName;

    private String scope = SCOPE_SINGLETON;

    private boolean singleton = true;

    private boolean prototype = false;

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
     * 设置bean的类型
     *
     * @param scope
     */
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    public boolean isPrototype() {
        return this.prototype;
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

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return beanClass.equals(that.beanClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass);
    }
}
