package com.tkzou.middleware.spring.config;

/**
 * BeanDefinition实例保存bean的信息，包括class类型、方法构造参数、是否为单例等，
 * 此处简化，只包含class类型
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
     * 构造器
     *
     * @param beanClass
     */
    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 默认构造器（这不需要，我们就使用有参构造器即可）
     * 注意：当自定义了构造器之后，该无参默认构造器需要显示声明才可以继续使用
     */
    public BeanDefinition() {
    }

    /**
     * 获取bean的class对象
     *
     * @return
     */
    public Class getBeanClass() {
        return beanClass;
    }

    /**
     * setBeanClass方法
     *
     * @param beanClass
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
