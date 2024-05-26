package com.tkzou.middleware.springframework.test.aop;

import com.tkzou.middleware.springframework.aop.*;
import com.tkzou.middleware.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.tkzou.middleware.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.tkzou.middleware.springframework.aop.framework.CglibAopProxy;
import com.tkzou.middleware.springframework.aop.framework.JdkDynamicAopProxy;
import com.tkzou.middleware.springframework.aop.framework.ProxyFactory;
import com.tkzou.middleware.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import com.tkzou.middleware.springframework.test.common.WorldServiceBeforeAdvice;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Before;
import org.junit.Test;

/**
 * aop动态代理测试
 *
 * @author zoutongkun
 */
public class DynamicProxyTest {

    private AdvisedSupport advisedSupport;

    /**
     * 测试的公共逻辑单独提出来，先执行！
     */
    @Before
    public void setup() {
        //目标对象，也即需要被代理的对象，
        //先封装为TargetSource对象，
        //再封装为AdvisedSupport对象
        WorldService worldService = new WorldServiceImpl();
        advisedSupport = new AdvisedSupport();
        TargetSource targetSource = new TargetSource(worldService);
//        拦截器/切面，包含目标方法和增强逻辑
        WorldServiceInterceptor methodInterceptor = new WorldServiceInterceptor();
        //指定切入点表达式
        MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* org.springframework.test.service" +
                ".WorldService.explode(..))").getMethodMatcher();
        advisedSupport.setTargetSource(targetSource);
        advisedSupport.setMethodInterceptor(methodInterceptor);
        advisedSupport.setMethodMatcher(methodMatcher);
    }

    /**
     * Jdk动态代理
     */
    @Test
    public void testJdkDynamicProxy() {
        WorldService proxy = (WorldService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        proxy.explode();
    }

    /**
     * Cglib动态代理
     */
    @Test
    public void testCglibDynamicProxy() {
        WorldService proxy = (WorldService) new CglibAopProxy(advisedSupport).getProxy();
        proxy.explode();
    }

    /**
     * 通过proxyTargetClass字段来选择何种动态代理
     */
    @Test
    public void testProxyFactory() {
        // 使用JDK动态代理
        advisedSupport.setProxyTargetClass(false);
        WorldService proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
        proxy.explode();

        // 使用CGLIB动态代理
        advisedSupport.setProxyTargetClass(true);
        proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
        proxy.explode();
    }

    /**
     * 测试切面
     *
     */
    @Test
    public void testAdvisor() {
        //目标对象，也即需要被代理的对象，
        WorldService worldService = new WorldServiceImpl();

        //切入点表达式
        //Advisor是Pointcut和Advice的组合
        String expression = "execution(* org.springframework.test.service.WorldService.explode(..))";
        Advice advice =
                new MethodBeforeAdviceInterceptor(new WorldServiceBeforeAdvice());
        //构建切面（切入点+通知）
        PointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor(expression, advice);
        ClassFilter classFilter = pointcutAdvisor.getPointcut().getClassFilter();
        //判断目标类是否匹配，也即是否被切中，切中才创建代理对象，并执行通知逻辑！
        if (classFilter.matches(worldService.getClass())) {
            //封装aop的核心逻辑--AdvisedSupport
            AdvisedSupport advisedSupport = new AdvisedSupport();
            //封装目标对象
            TargetSource targetSource = new TargetSource(worldService);
            advisedSupport.setTargetSource(targetSource);
            advisedSupport.setMethodInterceptor((MethodInterceptor) pointcutAdvisor.getAdvice());
            advisedSupport.setMethodMatcher(pointcutAdvisor.getPointcut().getMethodMatcher());
//			advisedSupport.setProxyTargetClass(true);   //JDK or CGLIB
            //创建代理对象--根据advisedSupport创建
            WorldService proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
            //执行目标方法，此时就会执行通知逻辑！
            proxy.explode();
        }
    }
}

