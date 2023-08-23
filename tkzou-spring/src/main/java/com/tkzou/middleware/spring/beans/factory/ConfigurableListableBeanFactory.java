package com.tkzou.middleware.spring.beans.factory;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.AutowireCapableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.ConfigurableBeanFactory;

/**
 * 不知道是干嘛，先写着！
 * 另外注意：一个接口能继承另一个或者多个接口，接口的继承和类之间的继承相似。
 * 接口的继承使用 extends 关键字，子接口继承父接口的方法。
 * 如果父接口中的默认方法有重名的，那么子接口需要重写一次。
 * 小结：
 * 1.类与类之间是单继承的，直接父类只有一个。
 * 2.但接口与接口之间是多继承的。
 * 3.类与接口之间是多实现的，一个类可以实现多个接口。
 *
 * @author :zoutongkun
 * @date :2023/8/23 10:45 下午
 * @description :
 * @modyified By:
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory,
        ConfigurableBeanFactory {

    /**
     * 根据名称查找BeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException 如果找不到BeanDefinition
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
