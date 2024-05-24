package com.tkzou.middleware.spring.test.aop;

import com.tkzou.middleware.spring.aop.AdvisedSupport;
import com.tkzou.middleware.spring.aop.MethodMatcher;
import com.tkzou.middleware.spring.aop.TargetSource;
import com.tkzou.middleware.spring.aop.aspectj.AspectJExpressionPointcut;
import com.tkzou.middleware.spring.aop.framework.CglibAopProxy;
import com.tkzou.middleware.spring.aop.framework.JdkDynamicAopProxy;
import org.junit.Before;
import org.junit.Test;

/**
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
}
