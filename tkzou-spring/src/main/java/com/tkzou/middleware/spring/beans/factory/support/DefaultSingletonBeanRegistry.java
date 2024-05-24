package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.DisposableBean;
import com.tkzou.middleware.spring.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取单例对象默认实现类
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:02
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    /**
     * 使用map存放单例对象
     * 因此易知，需要先将所有的单例bean对象存入
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 用于保存拥有销毁⽅法的bean
     * key为对应的beanName，value为对应的bean，是DisposableBeanAdapter类型，适配器模式！
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /**
     * 注册单例对象到map中
     * 注意：protected修饰符的作用范围为：相同包下可自由访问
     *
     * @param beanName
     * @param singletonObject
     */
    @Override
    public void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    /**
     * 从map中获取单例对象
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
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
     * 销毁所有的单例bean！！！
     * 易知，多例/原型bean不管哦！！！
     */
    public void destroySingletons() {
        //拿到所有需要执行的bean
        List<String> beanNames = new ArrayList<>(disposableBeans.keySet());
        //遍历，拿到所有注册（就包括了用户自定义的！）的该bean并依次执行对应的destroy方法！
        for (String beanName : beanNames) {
            //一边删除，一边执行对应的destroy方法！
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("在bean销毁前执行的destroy中，该bean'" + beanName + "' 的该方法执行异常：", e);
            }
        }
    }
}
