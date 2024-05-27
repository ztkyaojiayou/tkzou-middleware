package com.tkzou.middleware.springframework.beans.factory.support;

import cn.hutool.core.util.ObjectUtil;
import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.DisposableBean;
import com.tkzou.middleware.springframework.beans.factory.ObjectFactory;
import com.tkzou.middleware.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取单例对象默认实现类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:02
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    /**
     * 正在创建的单例对象
     * 用于判断是否产生循环依赖
     */
    private final Set singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));

    /**
     * 使用map存放最终的单例对象，也即平时说的ioc容器！！！
     * 因此易知，需要先将所有的单例bean对象存入
     * 也叫一级缓存
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 二级缓存，保存的是早期暴露的bean对象，
     * 可能是代理对象，也可能是原对象，
     * 但这不重要，主要和是否配置了切面有关！
     */
    protected Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * 三级缓存，保存的是ObjectFactory，即对象工厂
     * 产生的对象可能是代理对象，也可能是原对象，这不重要！
     * {key：beanName，value：objectFactory}
     * 也即每个bean都暴露一个对应的objectFactory!!!
     */
    private Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    /**
     * 用于保存拥有销毁⽅法的bean
     * key为对应的beanName，value为对应的bean，是DisposableBeanAdapter类型，适配器模式！
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /**
     * 注册单例对象到map中
     * 注意：protected修饰符的作用范围为：相同包下可自由访问
     * 维护三级缓存
     *
     * @param beanName
     * @param singletonObject
     */
    @Override
    public void addSingleton(String beanName, Object singletonObject) {
        //1.添加到一级缓存/单例池中！
        singletonObjects.put(beanName, singletonObject);
        //2.同时从二级缓存和三级缓存中移除，毕竟最终的单例对象已经创建出来啦！
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    /**
     * 从map中获取单例对象
     * 先从一级缓存取，若没有，再从二级缓存取
     * 若二级缓存也有，则从三级缓存取（三级缓存在此之前就已经设置好了，因此肯定有！），
     * 再将其放入二级缓存，同时从三级缓存中移除
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /**
     * 支持循环依赖的getSingleton方法
     *
     * @param beanName
     * @param allowEarlyReference
     * @return
     */
    @Override
    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        //1.先标记一下当前bean正在创建，目的是为了判断是否产生循环依赖
        beforeSingletonCreation(beanName);

        //2.先从一级缓存中获取
        Object singletonObject = singletonObjects.get(beanName);
        //3.若为空，并且该bean正在创建中，说明存在循环依赖，此时走二级缓存
        if (ObjectUtil.isEmpty(singletonObject) && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = earlySingletonObjects.get(beanName);
            //4.若二级缓存为空，并且允许循环依赖，则就要解决循环依赖，则从三级缓存中获取
            if (ObjectUtil.isEmpty(singletonObject) && allowEarlyReference) {
                //再从三级缓存取，肯定有，因为在此之前就已经设置好了！
                //注意：三级缓存中存放的是ObjectFactory，即创建代理对象的对象工厂
                //todo 源码中这里加锁了，且使用了双重检锁模式的单例模式，这里先不用了
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (ObjectUtil.isNotEmpty(singletonFactory)) {
                    //获取代理对象，此时就是执行getEarlyBeanReference方法，
                    // 且beanName, beanDefinition, finalBean这个参数也是有值的，
                    // 因为在添加时就已经被保存起来啦！！！
                    singletonObject = singletonFactory.getObject();
                    //5.将代理对象放入二级缓存
                    earlySingletonObjects.put(beanName, singletonObject);
                    //6.同时从三级缓存中移除
                    singletonFactories.remove(beanName);
                }
            }
        }

        //7.标记一下当前bean已经创建完成
        afterSingletonCreation(beanName);
        return singletonObject;
    }

    /**
     * 添加三级缓存
     * 但是这里添加的是ObjectFactory，即创建代理对象的对象工厂，而非代理对象本身！
     * 需要时才通过这个代理工厂来创建代理对象，并将其放入二级缓存中
     *
     * @param beanName
     * @param singletonFactory
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        singletonFactories.put(beanName, singletonFactory);
    }

    /**
     * 注册DisposableBean
     *
     * @param beanName
     * @param bean
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    /**
     * 销毁所有的单例bean（包括代理bean）！！！
     * 易知，多例/原型bean不管哦，因为也没保存呀！！！
     */
    public void destroySingletons() {
        //拿到所有需要执行的bean
        List<String> beanNames = new ArrayList<>(disposableBeans.keySet());
        //遍历，拿到所有注册（就包括了用户自定义的！）的该bean并依次执行对应的destroy方法！
        for (String beanName : beanNames) {
            //一边删除/取出，一边执行对应的destroy方法！
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("在bean销毁前执行的destroy中，该bean'" + beanName + "' 的该方法执行异常：", e);
            }
        }
    }

    /**
     * 判断当前bean是否正在创建中
     * 用于判断是否产生了循环依赖！
     *
     * @param beanName
     * @return
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * 单例bean创建前的执行逻辑
     *
     * @param beanName
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeansException(beanName);
        }
    }

    /**
     * 单例bean创建完后的执行逻辑
     *
     * @param beanName
     */
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

}
