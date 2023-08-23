package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.core.io.DefaultResourceLoader;
import com.tkzou.middleware.spring.core.io.ResourceLoader;

/**
 * BeanDefinitionReader的抽象实现
 * 这里要特别传达一个观念，那就是：抽象类是可以只实现接口中的部分方法的，
 * 未实现的方法依旧拥有，已经可以被调用，只是依旧是抽象的，具体交给子类实现，
 * 其实抽象类中已实现的方法也是子类在调！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/23 17:58
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    /**
     * 接口中有获取BeanDefinitionRegistry和ResourceLoader方法，
     * 那么一般而言在实现类中就应该定义了这两个变量；
     * 抽象类也是类，当然可以声明成员变量！
     * 另外，对于final类型的成员变量，无法被set
     */
    private final BeanDefinitionRegistry registry;
    private ResourceLoader resourceLoader;

    /**
     * 全参构造器
     * 问：抽象类并不能直接构造对象实例，那么它为什么可以声明构造器？它的作用又是什么？
     * 答：
     * 需要明确如下几点：
     * 1.对一个类中成员变量的赋值和使用该类是两码事，前者不需要实例化，后者才需要先实例化
     * 2.构造器并不是用于创建实例的，它的作用只是用于初始化成员变量，那么抽象类中当然可以有构造器
     * 3.只有构造器和new连在一块使用时，才是创建类的实例(对象)。
     * 4.那么抽象类中的构造方法何时被调用呢？抽象类中的构造器方法是在子类实例化时被调用
     * 且其顺序为：先调用父类构造方法---->再调用子类构造方法
     * 而若有多个构造方法时，是如何选择的呢？根据子类的构造方法决定，比如子类调用的构造方法带了一个参数，那么在实例化时会先调用父类（这里即为抽象类）的该相同参数的构造方法，
     * 同时要说明的是，此时父类的该构造方法可以再链式调用它的其他构造方法，这是语法！
     * 5.构造方法中的参数并不一定是和定义的成员变量的名称或个数相同，二者并没有关系，
     * 构造方法是一个自定义的方法，其名称固定，但参数并没有约束，只要完成了对成员变量初始化的功能即可！！！
     * 且也不要求其对所有的成员变量都初始化，因为类的成员变量在实例化的过程中本身就会被赋一个初始化值！！！
     * <p>
     * 6.可能为一个类写了多个构造器，有时可能想在一个构造器里面调用另外一个构造器，为了减少代码的重复，可用this关键字做到这一点。
     * 抽象类可以声明并定义构造函数。因为你不可以创建抽象类的实例，
     * 所以构造函数只能通过构造函数链调用（Java中构造函数链指的是从其他构造函数调用一个构造函数），
     * 例如，当你创建具体的实现类。
     * 如果你不能对抽象类实例化那么构造函数的作用是什么？
     * 它可以用来初始化抽象类内部声明的通用变量，并被各种实现使用。
     * 另外，即使你没有提供任何构造函数，编译器将为抽象类添加默认的无参数的构造函数，没
     * 有的话你的子类将无法编译，因为在任何构造函数中的第一条语句隐式调用super（），Java中默认超类的构造函数。
     * <p>
     * 参考：https://blog.csdn.net/weixin_49114503/article/details/115479895
     * https://blog.csdn.net/weixin_48345177/article/details/129878939
     *
     * @param registry
     * @param resourceLoader
     */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    /**
     * 该构造器中ResourceLoader选择默认实现
     *
     * @param registry
     */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        //直接调用上面的全参构造器，这个也是常用手法!即构造器的链式调用
        //调用的时机是在子类的实例化时
        //默认new了一个DefaultResourceLoader
        this(registry, new DefaultResourceLoader());
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * set方法
     *
     * @param resourceLoader
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 这些方法在该抽象类不实现，留给具体的子类实现
     * 但这不代表该类不拥有这些方法，是拥有的，
     * 可以在该类中使用this直接调用，只是具体的实现交给了子类！！！
     * 这个观念至关重要！！！
     *
     * @param locations
     * @throws BeansException
     */
//    @Override
//    public void loadBeanDefinitions(Resource resource) throws BeansException {
//
//    }
//
//    @Override
//    public void loadBeanDefinitions(String location) throws BeansException {
//
//    }

    @Override
    public void loadBeanDefinitions(String[] locations) throws BeansException {
//该方法属于重载方法，单个locations调用其另一个重载方法
        for (String location : locations) {
            this.loadBeanDefinitions(location);
        }

    }
}
