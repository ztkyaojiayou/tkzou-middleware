package com.tkzou.middleware.springframework.aop.framework.autoproxy;

import com.tkzou.middleware.springframework.aop.*;
import com.tkzou.middleware.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.tkzou.middleware.springframework.aop.framework.ProxyFactory;
import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValues;
import com.tkzou.middleware.springframework.beans.factory.BeanFactory;
import com.tkzou.middleware.springframework.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.tkzou.middleware.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于在当前bean未进行实例化时就根据配置的切面生成代理对象
 * 因为该类属于BeanPostProcessor，spring会统一进行初始化并注册到ioc容器中，
 * 无需自己手动new啦！！！
 * 具体在AbstractApplicationContext#registerBeanPostProcessors方法中
 * 对所有的后置处理器进行初始化并注册到ioc容器中
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
    /**
     * 保存需要或者已经提前暴露的bean名称
     * 暴露的可能是代理对象，也可能是原始对象，和切面配置有关！
     */
    private Set<Object> earlyProxyReferences = new HashSet<>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * bean实例化前执行
     * 参考DynamicProxyTest#testAdvisor
     *
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //更新：不在这里创建代理对象了，因为此时无法给bean设置type，移至bean初始化完成之后再对bean进行代理
        //具体也就是搬移到postProcessAfterInitialization这个方法中啦！
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        //对应代理对象，属性不做处理，直接就是返回代理对象即可
        return pvs;
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
        return bean;
    }

    /**
     * 根据切面规则为已经初始化完成了的bean创建代理对象
     * （也可能不需要代理，此时就返回原bean即可！）
     * 参考DynamicProxyTest#testAdvisor
     * 但这里是只负责在非循环依赖时才创建bean，但这也是最常见的情况。
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //即只负责非循环依赖时的情况，这也是最常见的情况
        //只有不产生循环依赖时才在在这里创建bean！！！
        //对于循环依赖的情况，就不在这里创建了，而在三级缓存中创建，
        //此时也需要先提前暴露三级缓存！
        if (!earlyProxyReferences.contains(beanName)) {
            //生成代理对象，但与切面配置有关，可能还是原对象！
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    /**
     * 提前暴露bean
     * 可能是代理对象，也可能就是原对象
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        //标记一下当前bean已经提前暴露了
        earlyProxyReferences.add(beanName);
        //真正创建需要提前暴露的对象
        return wrapIfNecessary(bean, beanName);
    }

    /**
     * 根据切面配置来决定是否生成代理对象
     *
     * @param bean
     * @param beanName
     * @return
     */
    protected Object wrapIfNecessary(Object bean, String beanName) {
        //此时普通的bean已经实例化和初始化完成
        //但对于代理类型的bean，则还未开始，在这里根据切面规则来进行代理！
        //目标对象就是传入的已经完成了实例化、依赖注入和初始化的bean！！！
        Class<?> beanClass = bean.getClass();
        //避免死循环
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
            ProxyFactory proxyFactory = new ProxyFactory();

            //todo 可以算是适配器模式！
            for (AspectJExpressionPointcutAdvisor advisor : candidatePointcutAdvisors) {
                //2.1判断当前bean是否符合切点规则，符合就创建代理对象
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();
                if (classFilter.matches(beanClass)) {
                    TargetSource targetSource = new TargetSource(bean);
                    proxyFactory.setTargetSource(targetSource);
                    proxyFactory.addAdvisor(advisor);
                    proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());


                    //更新：现在ProxyFactory已经继承AdvisedSupport了
//                    //封装AdvisedSupport
//                    AdvisedSupport advisedSupport = new AdvisedSupport();
//                    //封装目标对象
//                    TargetSource targetSource = new TargetSource(bean);
//                    advisedSupport.setTargetSource(targetSource);
//                    advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
//                    advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
//                    //2.2再根据这套条件创建代理对象并返回，不再判断后面的切面了！
//                    return new ProxyFactory(advisedSupport).getProxy();
                }
            }

            if (!proxyFactory.getAdvisors().isEmpty()) {
                return proxyFactory.getProxy();
            }

        } catch (Exception ex) {
            throw new BeansException("Error create proxy bean for: " + beanName, ex);
        }

        //2.3若不需要代理，则就返回原bean！！！
        return bean;
    }


}
