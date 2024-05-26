package com.tkzou.middleware.springframework.beans.factory;

/**
 * 与InitializingBean齐名，
 * 作用是当在bean销毁前，执行该接口中的唯一方法destroy()。
 * 补充：
 * 在Bean生命周期结束前调用destroy()方法做一些收尾工作，亦可以使用destroy-method,
 * 前者与Spring耦合高，使用类型强转.方法名()，效率高,后者耦合低，使用反射，效率相对低.
 * 要注意的是：
 * 多例的bean的生命周期不归Spring容器来管理，这里的DisposableBean中的方法是由Spring容器来调用的，所以如果一个多例实现了DisposableBean
 * 是没有啥意义的，因为相应的方法根本不会被调用，当然在XML配置文件中指定了destroy方法，也是没有意义的。所以，在多实例bean情况下，Spring不会自动调用bean的销毁方法，但初始化方法依旧生效！
 * 参考：https://mp.weixin.qq.com/s/MGDgiGd2wYBb5jn3h5NGAQ
 *
 * @author zoutongkun
 */
public interface DisposableBean {

    /**
     * 在单例bean（默认）销毁前会先执行该方法
     *
     * @throws Exception
     */
    void destroy() throws Exception;
}
