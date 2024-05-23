package com.tkzou.middleware.spring.beans.factory.support;

import cn.hutool.core.util.ClassUtil;
import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.DisposableBean;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * DisposableBeanAdapter对象是一个适配器，用于在销毁 bean 时执行必要的处理。它会将DisposableBean接口或@PreDestroy注解的方法转换为一个回调方法，以便在 bean
 * 销毁时执行。这种适配器模式允许非标准的 bean 销毁方法与 Spring 框架协同工作。
 * <p>
 * 在将DisposableBeanAdapter对象添加到一个DisposableBeanRegistry对象中时，Spring 会将该对象添加到一个 bean 销毁的注册表中。当需要销毁所有 bean 时，Spring
 * 就会从该注册表中获取所有需要销毁的 bean，并按照正确的顺序执行销毁任务。这样就可以确保应用程序的正确关闭。
 *
 * @author :zoutongkun
 * @date :2023/8/31 11:33 下午
 * @description :
 * @modyified By:
 */
public class DisposableBeanAdapter implements DisposableBean {

    public static final String DESTROY = "destroy";
    private final Object bean;
    private final String beanName;
    /**
     * 销毁方法的方法名
     */
    private final String destroyMethodName;

    /**
     * 构造器
     *
     * @param bean
     * @param beanName
     * @param beanDefinition
     */
    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    /**
     * 在单例bean（默认）销毁前会先执行该方法
     *
     * @throws Exception
     */

    @Override
    public void destroy() throws Exception {
        //1.先处理直接实现了DisposableBean接口的bean
        //instanceof方法常用
        if (bean instanceof DisposableBean) {
            //强转
            DisposableBean bean = (DisposableBean) this.bean;
            //执行销毁方法
            //todo：疑问：那这难道不是死循环吗？？？
            bean.destroy();
        }

        //2.若当前bean没有实现DisposableBean接口，则从该bean中也找出名称为destroy的方法作为销毁方法执行！
        //判断，避免同时继承自DisposableBean，且自定义方法与DisposableBean方法同名，销毁方法执行两次的情况
        if (StringUtils.isNotEmpty(this.destroyMethodName) && !(bean instanceof DisposableBean && DESTROY.equals(this.destroyMethodName))) {
            //执行自定义方法，参考afterPropertiesSet/初始化方法
            Method destroyMethod = ClassUtil.getPublicMethod(bean.getClass(), destroyMethodName);
            if (ObjectUtils.isEmpty(destroyMethod)) {
                throw new BeansException("' 在bean'" + beanName + "中找不到销毁方法 '" + destroyMethodName);
            }
            //执行该方法
            destroyMethod.invoke(bean);
        }
    }
}
