package com.tkzou.middleware.springframework.beans.factory.config;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValues;

/**
 * bean实例化前后的处理器
 * 此时可以对beanDefinition进行修改！
 * 最常使用的就是在这里进行aop织入，即根据配置来觉得是否生成代理对象！
 * 具体的实现类为DefaultAdvisorAutoProxyCreator，
 * 如果InstantiationAwareBeanPostProcessor处理阶段返回代理对象，会导致短路，不会继续⾛
 * 原来的创建bean的流程，
 *
 * @author zoutongkun
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 在实例化bean之前调用
     *
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    /**
     * bean实例化之后，设置属性之前执行
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;

    /**
     * bean实例化之后，设置属性之前执行
     * 比如处理/解析@Value和@Autowired等注解
     *
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;

}
