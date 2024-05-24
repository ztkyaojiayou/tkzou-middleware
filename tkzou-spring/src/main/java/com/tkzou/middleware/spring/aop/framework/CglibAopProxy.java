package com.tkzou.middleware.spring.aop.framework;

import com.tkzou.middleware.spring.aop.AdvisedSupport;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib动态代理工厂
 * 该类也属于代理对象工厂+代理增强逻辑二合一！
 *
 * @author :zoutongkun
 * @date :2024/5/24 9:58 下午
 * @description :
 * @modyified By:
 */
public class CglibAopProxy implements AopProxy {

    private AdvisedSupport advised;

    public CglibAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        //使用cglib方式生成代理对象
        Enhancer enhancer = new Enhancer();
        //指定被代理对象的父类
        enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
        //指定增强逻辑
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return null;
    }

    /**
     * 增强逻辑类，内部类
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {
        /**
         * aop核心对象
         */
        private AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        /**
         * 增强逻辑方法
         *
         * @param o
         * @param method
         * @param objects
         * @param methodProxy 用于执行目标方法，这是cglib自己封装的方法，比反射执行的效率更高
         * @return
         * @throws Throwable
         */
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            //封装CglibMethodInvocation，本质就是个ReflectiveMethodInvocation，也即目标方法的快照
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation(advised.getTargetSource().getTarget(),
                    method, objects, methodProxy);
            //同jdk的实现逻辑，先判断一下切入点表达式，看是否需要执行增强逻辑！
            if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                //1.执行带有增强逻辑和原方法的方法
                return advised.getMethodInterceptor().invoke(methodInvocation);
            }
            //2.执行原方法
            return methodInvocation.proceed();
        }

        /**
         * 目标方法快照，内部类
         * 为什么要重写一下？
         */
        private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
            private MethodProxy methodProxy;

            public CglibMethodInvocation(Object target, Method method, Object[] objects, MethodProxy methodProxy) {
                super(target, method, objects);
                this.methodProxy = methodProxy;
            }

            /**
             * 执行目标方法
             * todo 为什么需要使用methodProxy去执行目标方法？直接使用method.invoke不行吗？？？
             *   答案是可以的，只是使用这种方式效率更高，因为cglib内部已经封装了反射的逻辑，所以使用methodProxy效率更高！！！但其实源码中还是使用父类的proceed方法来执行的。。。
             * 参考：https://blog.csdn.net/qq_43799161/article/details/123604338
             *
             * @return
             * @throws Throwable
             */
            @Override
            public Object proceed() throws Throwable {
                return methodProxy.invoke(this.target, this.arguments);
            }
        }
    }

}
