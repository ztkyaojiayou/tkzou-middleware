package com.tkzou.middleware.spring.context.support;

import com.tkzou.middleware.spring.beans.BeansException;

/**
 * xml文件的应用上下文
 * 到头了，终于是一个具体的实现类了，
 * 作用也是完成父类的遗愿，即完成xml文件的解析，
 * 同时调用/触发抽象父类中的著名方法refresh()方法！！！
 * 这个思路太重要了，几乎所有的源码都是这个思路！！！
 * 且：父类的任何方法的调用都一定是通过具体的实现类或子类来完成的，
 * 因为抽象类不能创建对象，也就无法完完成方法的调用！！！
 *
 * @author :zoutongkun
 * @date :2023/8/29 12:26 上午
 * @description :
 * @modyified By:
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    /**
     * xml配置文件的路径，可多个
     * 因为是最终实现类，因此就需要准备最足的已知量，比如这里的xml配置文件
     * 因为我们就是通过xml配置文件来进行配置的！！！
     */
    private String[] configLocations;

    /**
     * 再定义一个只要一个配置文件的构造器，本质还是调用原构造器
     *
     * @param configLocations
     * @throws BeansException
     */
    public ClassPathXmlApplicationContext(String configLocations) throws BeansException {
        this(new String[]{configLocations});
    }

    /**
     * 构造器
     * 有成员变量机就必须有构造器
     * 同时，在这里需要做一件超级无敌大的大事，那就是刷新ioc容器或叫上下文！！！
     * 也即在解析xml文件的同时创建ioc容器！！！
     * 看似只是一个构造器，实则翻江倒海！！！
     * @param configLocations
     * @throws BeansException
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this.configLocations = configLocations;
        //创建/刷新上下文！！！类似于初始化，这一步无比重要！！！
        this.refresh();
    }

    /**
     * 获取xml配置文件路径，加载BeanDefinition，
     *
     * @return
     */
    @Override
    protected String[] getConfigLocations() {
        return this.configLocations;
    }
}
