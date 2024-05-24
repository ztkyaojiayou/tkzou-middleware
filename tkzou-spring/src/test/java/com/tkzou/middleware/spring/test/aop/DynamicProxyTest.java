package com.tkzou.middleware.spring.test.aop;

import com.tkzou.middleware.spring.aop.AdvisedSupport;
import com.tkzou.middleware.spring.aop.MethodMatcher;
import com.tkzou.middleware.spring.aop.TargetSource;
import com.tkzou.middleware.spring.aop.aspectj.AspectJExpressionPointcut;
import com.tkzou.middleware.spring.aop.framework.JdkDynamicAopProxy;
import org.junit.Test;

/**
 * @author zoutongkun
 */
public class DynamicProxyTest {

    @Test
    public void testJdkDynamicProxy() throws Exception {
        //目标对象，也即需要被代理的对象，
        //先封装为TargetSource对象，
        //再封装为AdvisedSupport对象
        WorldService worldService = new WorldServiceImpl();

        AdvisedSupport advisedSupport = new AdvisedSupport();
        TargetSource targetSource = new TargetSource(worldService);
        WorldServiceInterceptor methodInterceptor = new WorldServiceInterceptor();
        //切入点表达式
        MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* org.springframework.test.service.WorldService.explode(..))").getMethodMatcher();
        advisedSupport.setTargetSource(targetSource);
        advisedSupport.setMethodInterceptor(methodInterceptor);
        advisedSupport.setMethodMatcher(methodMatcher);

        WorldService proxy = (WorldService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        proxy.explode();
    }
}
