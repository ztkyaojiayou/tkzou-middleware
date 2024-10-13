package com.tkzou.middleware.springframework.context.support;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.tkzou.middleware.springframework.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.springframework.context.ApplicationEvent;
import com.tkzou.middleware.springframework.context.ApplicationListener;
import com.tkzou.middleware.springframework.context.ConfigurableApplicationContext;
import com.tkzou.middleware.springframework.context.event.ApplicationEventMulticaster;
import com.tkzou.middleware.springframework.context.event.ContextClosedEvent;
import com.tkzou.middleware.springframework.context.event.ContextRefreshedEvent;
import com.tkzou.middleware.springframework.context.event.SimpleApplicationEventMulticaster;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import com.tkzou.middleware.springframework.core.io.DefaultResourceLoader;

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
     * 一些写死的bean名称，因此我们在自定义bean时id必须与之相同，
     * 否则设置的就是另一个bean了，那么就不生效了！
     */
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    public static final String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
    /**
     * 事件广播器
     */
    private ApplicationEventMulticaster applicationEventMulticaster;

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
        //2.添加ApplicationContextAwareProcessor，用于处理Aware接口
        //让继承自ApplicationContextAware的bean能感知bean
        //也即初始化了该后置处理器！
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        //2.在bean实例化之前，先执行BeanFactoryPostProcessor
        //
        invokeBeanFactoryPostProcessor(beanFactory);
        //3.再在bean实例化之前注册BeanPostProcessor，也即用于执行对应的那两个方法
        // 此时可以修改beanDefinition的属性值，如对属性中占位符的处理！
        registerBeanPostProcessor(beanFactory);

        //5.初始化事件发布者，此时会把创建的事件发布者对象直接添加到ioc容器中
        initApplicationEventMulticaster();
        //6.注册事件监听器，此时也会触发getBean，只是是针对单个bean而已！
        registerListeners();

        //7.注册类型转换器和提前实例化单例bean--核心
        finishBeanFactoryInitialization(beanFactory);

        //8.发布容器刷新完成事件
        finishRefresh();
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        //设置类型转换器，也即初始化类型转换器
        if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME)) {
            Object conversionService = beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME);
            if (conversionService instanceof ConversionService) {
                beanFactory.setConversionService((ConversionService) conversionService);
            }
        }

        //4.最后提前实例化所有的单例bean--核心
        //此时就是扫描所有bean并将其添加到ioc容器中
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    protected void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 注册所有的事件监听者
     * 也即实现了ApplicationListener接口的bean
     * 也即前提是ioc容器已经初始化完毕
     */
    protected void registerListeners() {
        //直接从ioc容器中获取所有实现了ApplicationListener接口的bean，
        // 若没有则也会触发创建bean的逻辑，因此无脑get即可！
        getBeansOfType(ApplicationListener.class).values()
                .forEach(applicationListener -> applicationEventMulticaster.addApplicationListener(applicationListener));
    }

    /**
     * 初始化事件发布者并将其直接添加到ioc容器中
     * 使用的就是默认的SimpleApplicationEventMulticaster对象
     */
    protected void initApplicationEventMulticaster() {
        //得到的就是DefaultListableBeanFactory，具体是在子类中完成初始化的
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        //手动将这个对象注册到ioc中
        beanFactory.addSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
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
        //发布一下容器关闭事件
        publishEvent(new ContextClosedEvent(this));

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
        //1.先创建所有的BeanPostProcessor型的bean
        //此时因为还没有集中进行bean初始化，因此在get时会单独触发对应的create方法，
        //并将创建完成的bean加入到ioc容器中！！！
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
        //1.先创建所有的BeanFactoryPostProcessor型的bean
        //此时就会直接创建这些bean并将其加入到ioc容器中！
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap =
                beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        //2.再依次执行，可以修改beanDefinition的属性值，如对属性中占位符的处理！
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

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(requiredType);
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

    /**
     * 发布当前事件
     * 所有的监听者已经在等着这个事件发布啦！
     *
     * @param event
     */
    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }
}
