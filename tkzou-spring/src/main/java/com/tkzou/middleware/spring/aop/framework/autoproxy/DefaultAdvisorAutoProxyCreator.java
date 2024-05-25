package com.tkzou.middleware.spring.aop.framework.autoproxy;

import com.tkzou.middleware.spring.aop.*;
import com.tkzou.middleware.spring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.tkzou.middleware.spring.aop.framework.ProxyFactory;
import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.BeanFactory;
import com.tkzou.middleware.spring.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;
import java.util.List;

/**
 * 用于在当前bean未进行实例化时就根据配置的切面生成代理对象
 *
 * @author :zoutongkun
 * @date :2024/5/25 2:01 下午
 * @description :
 * @modyified By:
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    /**
     * 把ioc容器对象通过aware接口注入进来备用！
     * 这样就可以直接单独获取或创建bean啦，即使此时refresh方法没有执行！！！
     * 因为单独执行getBean方法就可以创建bean并将其注入到ioc容器中！
     */
    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * bean实例化前执行
     * 核心方法，返回代理对象
     * 参考DynamicProxyTest#testAdvisor
     *
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //此时bean还未进行实例化
        if (isInfrastructureClass(beanClass)) {
            return null;
        }

        //1.获取/初始化项目中配置或自定义的所有AspectJExpressionPointcutAdvisor切面
        //此时因为ioc容器还没有初始化完成，因此本质上就是单独创建这些bean并将其注入到ioc容器中
        Collection<AspectJExpressionPointcutAdvisor> candidatePointcutAdvisors =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        //2.遍历所有切面，判断是否有切面要切当前类，若有，则为当前bean创建代理对象
        //找到一个作用于当前类的切面即可！
        try {
            //todo 可以算是适配器模式！
            for (AspectJExpressionPointcutAdvisor advisor : candidatePointcutAdvisors) {
                //2.1判断当前bean是否符合切点规则，符合就创建代理对象
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();
                if (classFilter.matches(beanClass)) {
                    //封装AdvisedSupport
                    AdvisedSupport advisedSupport = new AdvisedSupport();
                    //其实也可以直接调用getBean方法创建该bean，只是步骤多了一些
                    //                Object bean1 = beanFactory.getBean(beanName);
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                    Object bean = beanFactory.getInstantiationStrategy().instantiate(beanDefinition);
                    //封装目标对象
                    TargetSource targetSource = new TargetSource(bean);
                    advisedSupport.setTargetSource(targetSource);
                    advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
                    advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
                    //2.2再根据这套条件创建代理对象并返回，不再判断后面的切面了！
                    return new ProxyFactory(advisedSupport).getProxy();
                }
            }
        } catch (Exception ex) {
            throw new BeansException("Error create proxy bean for: " + beanName, ex);
        }

        return null;
    }

    /**
     * 判断当前bean是否是切面
     * 若是，则跳过，否则死循环了呀！
     *
     * @param beanClass
     * @return
     */
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass)
                || Pointcut.class.isAssignableFrom(beanClass)
                || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //此时bean已经实例化完成
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //此时bean已经实例化完成
        return null;
    }

}
