package com.tkzou.middleware.springframework.aop;

import com.tkzou.middleware.springframework.aop.framework.AdvisorChainFactory;
import com.tkzou.middleware.springframework.aop.framework.DefaultAdvisorChainFactory;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封装aop的核心逻辑，包括目标对象、方法增强拦截器（包括目标方法快照信想和诸如前置/后置通知等增强逻辑）、切入点等。
 * 核心类
 *
 * @author zoutongkun
 */
public class AdvisedSupport {
    /**
     * 是否使用cglib代理，用于区分使用哪种代理方式！
     * 这里为false，易知默认是使用jdk动态代理的！
     * 更新：改为了true
     */
    private boolean proxyTargetClass = true;

    /**
     * 目标对象
     */
    private TargetSource targetSource;
    /**
     * 用于拦截目标方法的接口
     * 目标方法的快照信息则是封装在MethodInvocation中，
     * 在invoke方法中会做两件事：
     * 1.执行MethodInvocation中的proceed方法
     * 2.在它的前后还可以执行自定义的逻辑，具体而言，只需自己实现该接口！
     * 这就是aop的效果！
     * 也即执行前后也即在目标方法执行前后添加自定义的逻辑啦！！！
     * 这就是aop的核心思想，也即对方法进行拦截，在方法执行前后添加自定义的逻辑
     */
    private MethodInterceptor methodInterceptor;
    /**
     * 切入点方法匹配器
     * 只有符合该匹配规则的方法才会被拦截，这是最大的前提！
     */
    private MethodMatcher methodMatcher;

    /**
     * 用于保存每个方法对应的aop通知链
     * 也即每个方法在执行前，需要先执行这些aop通知！
     */
    private transient Map<Integer, List<Object>> methodCache;
    /**
     * aop通知链工厂
     * 用于获取符合某个方法的aop通知链
     */
    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();
    /**
     * 所有的aop通知链集合
     * 下一步就是找出符合某个方法的aop通知链
     * 具体就是在AdvisorChainFactory接口中完成的！
     * 然后保存在当前类的methodCache缓存中
     */
    private List<Advisor> advisors = new ArrayList<>();

    public AdvisedSupport() {
        this.methodCache = new ConcurrentHashMap<>(32);
    }

    /**
     * 获取指定方法的所有aop通知/拦截器链
     * 核心方法，通过AdvisorChainFactory接口完成
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        //缓存的key
        Integer cacheKey = method.hashCode();
        List<Object> cached = this.methodCache.get(cacheKey);
        if (cached == null) {
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                    this, method, targetClass);
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }

    /**
     * 添加aop通知
     *
     * @param advisor
     */
    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }


}
