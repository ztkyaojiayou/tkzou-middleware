package com.tkzou.middleware.springframework.context.support;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 再次定义一个抽象子类
 * 主要用于完成父类中beanFactory的赋值，易知只需定义一个成员变量即可
 * 同时实现父类中的抽象方法，加载BeanDefinition
 * 注意：我们几乎不太可能去重写抽象父类中已经实现的方法，基本上是去实现抽象父类中未实现的方法！！！
 *
 * @author :zoutongkun
 * @date :2023/8/29 12:01 上午
 * @description :
 * @modyified By:
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    /**
     * 定义factory成员变量，完善父类的逻辑
     */
    private DefaultListableBeanFactory beanFactory;

    /**
     * 实现父类的抽象方法
     * 创建beanFactory，同时加载BeanDefinition
     *
     * @throws BeansException
     */
    @Override
    protected void refreshBeanFactory() throws BeansException {
        //1.创建beanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        //赋值给成员变量，完成初始化
        this.beanFactory = beanFactory;
        //2.加载BeanDefinition
        loadBeanDefinition(beanFactory);
    }

    /**
     * 加载BeanDefinition
     * 抽象方法，由具体子类实现
     * todo 为什么？直接实现不行吗? 因为可以有多种方式来解析获取，比如xml或注解的方式！
     *
     * @param beanFactory
     * @throws BeansException
     */
    protected abstract void loadBeanDefinition(DefaultListableBeanFactory beanFactory) throws BeansException;

    /**
     * 创建DefaultListableBeanFactory
     * 因为是一个实现类，因此直接new了！
     * 个人觉得这种方法属于过度封装了
     *
     * @return
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }


    /**
     * 实现父类的抽象方法
     * 获取ConfigurableListableBeanFactory的BeanFactory
     *
     * @return
     */
    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
