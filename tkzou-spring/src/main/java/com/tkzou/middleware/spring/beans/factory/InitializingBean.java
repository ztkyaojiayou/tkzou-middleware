package com.tkzou.middleware.spring.beans.factory;

/**
 * 著名接口
 * 它的作用是为bean提供初始化方法的方式，它只包括afterPropertiesSet方法，
 * 凡是继承该接口的类，在初始化bean的时候会执行该方法！
 * 补充：
 * 1：spring为bean提供了两种初始化bean的方式，实现InitializingBean接口，实现afterPropertiesSet方法，或者在配置文件中同过init-method指定，
 * 两种方式可以同时使用
 * 2：实现InitializingBean接口是直接调用afterPropertiesSet方法，比通过反射调用init-method指定的方法效率相对来说要高点。但是init-method方式消除了对spring的依赖
 * 3：如果调用afterPropertiesSet方法时出错，则不调用init-method指定的方法。
 * 4：和postConstruct注解的区别：主要是执行顺序不同，
 * PostConstruct-->InitializingBean-->xml中配置init方法
 * 5：bean的初始化方法汇总：
 * 5.1首先是指定自定义初始化方法@Bean(init-Method)
 * 5.2实现InitializingBean接口重写afterPropertySet()
 * 5.3bean实现BeanPostProcessor接口，在bean初始化方法执行前后分别执行postProcessBeforeInitialization和postProcessAfterInitialization
 * 5.4@PostConstruct标注初始化对应有destroy的方法
 *
 * @author zoutongkun
 */
public interface InitializingBean {
    /**
     * 在初始化bean时会执行该方法
     *
     * @throws Exception
     */
    void afterPropertiesSet() throws Exception;
}

