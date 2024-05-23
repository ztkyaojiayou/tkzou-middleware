package com.tkzou.middleware.spring.context.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.tkzou.middleware.spring.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.spring.context.ConfigurableApplicationContext;
import com.tkzou.middleware.spring.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * 应用上下文的一个抽象子类
 * 抽象应用上下文
 *
 * @author :zoutongkun
 * @date :2023/8/28 11:30 下午
 * @description :
 * @modyified By:
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    /**
     * 最著名的方法：刷新容器！！！
     * 务必掌握！！！
     *
     * @throws BeansException
     */
    @Override
    public void refresh() throws BeansException {
        //1.先创建beanFactory，同时加载BeanDefinition
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        //2.在bean实例化之前，先执行BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessor(beanFactory);
        //3.再在bean实例化之前注册BeanPostProcessor，也即用于执行对应的那两个方法
        registerBeanPostProcessor(beanFactory);
        //4.最后提前实例化所有的单例bean
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 关闭上下文
     */
    @Override
    public void close() {
        //执行关闭上下文方法
        doClose();
    }

    protected void doClose() {
        //销毁所有bean
        destroyBeans();
    }

    protected void destroyBeans() {
        this.getBeanFactory().destroySingletons();
    }

    /**
     * 向jvm中注册一个钩子方法，用于在jvm关闭之前执行以关闭容器等操作
     * 参考：https://my.oschina.net/huangcongmin12/blog/357538
     */
    @Override
    public void registerShutdownHock() {
        //1.定义钩子方法的逻辑
        //就是调用spring的close方法！
        Thread shutdownHookThread = new Thread(this::doClose);
        //2.再注册进jvm
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }

    /**
     * 注册BeanPostProcessor
     *
     * @param beanFactory
     */
    protected void registerBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        //1.先获取所有的BeanPostProcessor型的bean
        //问：为什么此时就有这些bean？在refreshBeanFactory方法中会完成！
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        //2.再依次注册
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    /**
     * 在bean实例化之前，先执行BeanFactoryPostProcessor
     *
     * @param beanFactory
     */
    protected void invokeBeanFactoryPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        //1.先获取所有的BeanFactoryPostProcessor型的bean
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap =
                beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        //2.再依次执行
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 定义抽象方法，由具体子类实现
     * 创建beanFactory，同时加载BeanDefinition
     *
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;

    /**
     * 定义抽象方法
     * 获取ConfigurableListableBeanFactory的BeanFactory
     * 下面这些方法的实现都都依赖它，具体由子类实现！
     *
     * @return
     */
    public abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * 获取bean
     *
     * @param beanName
     * @return
     * @throws BeansException bean不存在时抛出自定义异常
     */
    @Override
    public Object getBean(String beanName) throws BeansException {

        return getBeanFactory().getBean(beanName);
    }

    /**
     * 根据名称和类型查找bean
     *
     * @param name
     * @param requiredType
     * @return
     * @throws BeansException
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    /**
     * 获取指定类型的所有bean实例
     *
     * @param type
     * @return
     * @throws BeansException
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取所有bean的名称
     *
     * @return
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }
}
