package com.tkzou.middleware.mybatis.core.plugin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

/**
 * <p> 插件代理 </p>
 * 也是代理模式
 * 用于代理插件/拦截器/Interceptor
 * 主要是定义增强逻辑！但一般也会融合生成代理对象的方法！
 * 相当于MapperProxy类
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 01:09
 */
public class Plugin implements InvocationHandler {
    /**
     * 目标类，也即被代理的类
     */
    private final Object target;
    /**
     * 插件，做到可插拔，更灵活
     * 可以使用list吧？是的，但是使用的是职责链模式！
     * 具体在InterceptorChain中实现！
     */
    private final Interceptor interceptor;
    /**
     * 目标拦截器/插件上声明的需要执行拦截器的方法
     * 也即只有当执行目标类中的这些方法时才走拦截器
     */
    private final Set<Method> methods;

    public Plugin(Object target, Interceptor interceptor, Set<Method> methods) {
        this.target = target;
        this.interceptor = interceptor;
        this.methods = methods;
    }

    /**
     * 增强逻辑
     * 就是插件逻辑！
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //只有当执行methods中的方法时才走拦截器
        //拦截需要被拦截的方法，同时返回目标方法的返回值！
        //易知，执行的主动权转向了拦截器本身，更易控制！
        if (this.methods != null && this.methods.contains(method)) {
            //执行拦截逻辑
            //把当前要执行的方法封装/保存为Invocation，
            // 理解为一个快照即可，目的是便于灵活处理，
            // 这样就可以在其前后加逻辑了！
            // 但要注意的是，并没有对原方法执行后的结果进行增强处理，
            // 若真想处理，则较为困难，当然也没这个需求！
            return this.interceptor.intercept(new Invocation(this.target, method, args));
        } else {
            //否则就是正常执行，相当于没有增强
            //返回目标方法执行的结果
            return method.invoke(this.target, args);
        }
    }

    /**
     * 即获取代理对象
     *
     * @param target      目标类，也即被代理的类，比如SimpleExecutor
     * @param interceptor 拦截器，理论上只需要一个目标类就可以了，为什么还需要它？
     *                    因为增强逻辑中需要它，且目的也是为了执行它的逻辑！
     *                    易知，只要你需要，什么对象都可以往里面加呀！
     * @param <T>
     * @return 返回代理对象
     */
    public static <T> T wrap(Object target, Interceptor interceptor) {

//        获取具体插件/拦截器上的@Intercepts注解的信息
        //key:需要被拦截的类，value：被拦截的类中需要被拦截的方法
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        //是否走代理标志
        boolean isProxy = false;
        //解析出目标类的哪些方法需要被拦截（也是增强逻辑的一部分）
        Set<Method> methods = null;
        //遍历判断当前类是否需要被该拦截器拦截！
        for (Class<?> curClazz : signatureMap.keySet()) {
            //如target：SimpleExecutor->Executor
            //curClazz就是拦截器上标记需要被拦截的接口
            // 比如：若配置了Executor.class，那么就说明目标类需要被当前拦截器拦截！
            //补充：A.isAssignableFrom(B)：判断A是否是B的父类或接口
            //（而不是反过来，理解为A能否由B强转过来即可！），
            // 两个参数都是clazz对象，都可以是接口的clazz！
            if (curClazz.isAssignableFrom(target.getClass())) {
                isProxy = true;
                methods = signatureMap.get(curClazz);
                break;
            }
        }

        //判断是否需要代理
        if (isProxy) {
            // 需要代理，因为拦截器上配置了目标类
            // 于是生成代理对象，代理逻辑就在Plugin中，需要被拦截的方法就会走上面的invoke方法！
            return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new Plugin(target, interceptor, methods));
        } else {
            // 不需要代理，即不走拦截器，就直接执行目标方法，返回的也是原始对象
//            因为当前拦截器上未配置目标类，即无需拦截
            return (T) target;
        }
    }

    /**
     * 获取具体插件/拦截器上的@Intercepts注解的信息
     * 如SqlLogInterceptor拦截器
     *
     * @param interceptor
     * @return key：拦截到的类，value：该类中需要被拦截的方法！
     */
    @SneakyThrows
    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        // class -> query,update
        Map<Class<?>, Set<Method>> result = Maps.newHashMap();
        //拿到@Intercepts注解的信息
        Intercepts intercepts = interceptor.getClass().getAnnotation(Intercepts.class);
        Signature[] signatures = intercepts.value();
        for (Signature signature : signatures) {
            //拦截到的类，如SimpleExecutor
            Class<?> type = signature.type();
            String methodName = signature.method();
            Class<?>[] args = signature.args();
            //获取该类中需要被拦截的方法对象，如SimpleExecutor中的query方法！
            Method method = type.getMethod(methodName, args);
            Set<Method> methods = result.get(type);
            if (methods == null) {
                result.put(type, Sets.newHashSet(method));
            } else {
                methods.add(method);
            }
        }
        return result;
    }

}
