package com.tkzou.middleware.springframework.beans.factory;

import com.tkzou.middleware.springframework.beans.BeansException;

import java.util.Map;

/**
 * bean工厂接口的另一个子接口
 * 目前还不知道哪里有使用到它，先写！
 * 定义了两个关于bean的方法
 *
 * @author :zoutongkun
 * @date :2023/8/23 10:35 下午
 * @description :
 * @modyified By:
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 获取指定类型的所有bean实例
     * 若ioc容器中还没有时，会自动创建并加入到ioc容器中！
     * 通常是spring框架在还未集中初始化bean前调用才会触发创建流程，
     * 比如注册一些spring自己的组件，如后置处理器！
     * 对于我们使用者，在使用该方法时，ioc容器肯定已经初始化完毕，
     * 因此直接就可以从ioc容器中获取到bean实例啦！！！
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 获取所有bean的名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();
}
