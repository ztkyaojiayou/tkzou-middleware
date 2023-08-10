package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;

/**
 * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象工厂类
 * 这是个抽象类，父类的getBeanDefinition方法交由子类实现
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 15:02
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 默认使用无参构造函数实例化对象
     * 经测试，这里使用新策略CglibSubclassingInstantiationStrategy也是ok的！！！
     */
    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();

    /**
     * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象
     * 1.根据class对象，利用反射，生成bean对象
     * 2.将该对象与beanName绑定，并存入bean容器中！！！
     * 3.同时，返回该bean对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        return doCreateBean(beanName, beanDefinition);
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        //1.获取class对象
//        Class beanClass = beanDefinition.getBeanClass();
        Object bean;
        try {
            //2.根据反射生成bean对象
            //todo 易知这里只适⽤于bean有⽆参构造函数的情况
//            bean = beanClass.newInstance();
            //更新：因为新增了专门的接口，有两个实现类，这里做兼容
            bean = createBeanInstance(beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        //3.将该beanName和生成的bean对象绑定，并存入bean容器中！！！
        addSingleton(beanName, bean);

        //4.同时返回该生成的bean对象
        return bean;
    }

    /**
     * 生成bean实例
     * 默认使用无参构造器生成
     *
     * @param beanDefinition
     * @return
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition) {
        return instantiate(beanDefinition);
    }

    /**
     * 生成bean实例
     * 该方法其实也就是一种简单的封装
     *
     * @param beanDefinition
     * @return
     * @throws BeansException
     */
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        return getInstantiationStrategy().instantiate(beanDefinition);
    }

    /**
     * 获取bean实现的策略
     * <p>
     * 注意：没有什么高大上，就是根据高内聚的原则对类内的成员变量做的简单的封装，
     * 且可以通过idea的快捷键Alt+insert的Delegate Methods生成！！！
     * 其实就是get方法！！！
     *
     * @return
     */
    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    /**
     * set方法
     *
     * @param instantiationStrategy
     */
    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
}
