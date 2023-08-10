package com.tkzou.middleware.spring.beans.factory.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 使用CGLIB动态生成子类
 * CGLIB（CODE GENERLIZE LIBRARY）代理是针对类实现代理，
 * 主要是对指定的类生成一个子类，覆盖其中的所有方法，所以该类或方法不能声明称final的。
 * 参考：https://www.jianshu.com/p/abb674bb418c
 * https://blog.csdn.net/weixin_43739348/article/details/129637949
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 9:34
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        //方式1：
        //1.创建Enhancer对象，类似于JDK动态代理的Proxy类，下一步就是设置几个参数
        Enhancer enhancer = new Enhancer();
        //2.设置代理对象的父类的字节码对象(Class类型的对象) , 指定代理对象的父类
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        //3.设置回调函数 , 实现调用代理对象的方法时最终都会执行MethodInterceptor的子实现类的intercept方法,
        // 在这个函数中利用反射完成任意目标类方法的调用
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * 用于对父类中的方法进行增强的代码
             * MethodInterceptor是主要的方法拦截类，它是Callback接口的子接口，需要用户实现。
             * 在这个方法中利用反射完成任意目标类中的方法的调用，同时加上自定义的逻辑，实现增强！！！
             * @param proxy 代理对象
             * @param method 真实对象中（也即父类/目标对象）的方法的Method实例对象，
             *               如可以通过method.getName()获取方法名，实现对某一个特定的方法这个增强！！！
             * @param methodParams 当前方法的实际参数 , 可以是0到N个，根据父类中的方法定义获取参数即可
             * @param proxyMethod 代理对象中的方法的method实例
             * @return 返回值类型是该方法的返回值类型
             * @throws Throwable
             */
            @Override
            public Object intercept(Object proxy, Method method, Object[] methodParams, MethodProxy proxyMethod) throws Throwable {
                //返回该方法的返回值类型，可以强转为目标类型
                return proxyMethod.invokeSuper(proxy, methodParams);
            }
        });

        //4.设置完参数后就可以生成代理对象了 ,默认返回的是Object类型 , 可以进行强转 , 创建真正的代理对象
        //生成并返回一个代理对象，返回的对象其实就是一个封装了“实现类”的代理类，是实现类的实例。
        return enhancer.create();

//方式2：（更简洁）
//        return Enhancer.create(beanDefinition.getBeanClass(), (MethodInterceptor) (o, method, objects, methodProxy)
//        -> methodProxy.invokeSuper(o, objects));
    }
}
