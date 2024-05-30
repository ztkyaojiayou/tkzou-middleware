package com.tkzou.middleware.springframework.aop.framework;

import com.tkzou.middleware.springframework.aop.AdvisedSupport;
import com.tkzou.middleware.springframework.aop.Advisor;
import com.tkzou.middleware.springframework.aop.MethodMatcher;
import com.tkzou.middleware.springframework.aop.PointcutAdvisor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的aop链工厂，用于创建aop链
 *
 * @author :zoutongkun
 * @date :2024/5/30 10:31 下午
 * @description :
 * @modyified By:
 */
public class DefaultAdvisorChainFactory implements AdvisorChainFactory {

    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method,
                                                                    Class<?> targetClass) {
        //所有的aop通知/拦截器链
        Advisor[] advisors = config.getAdvisors().toArray(new Advisor[0]);
        //符合当前方法的aop通知链
        List<Object> interceptorList = new ArrayList<>(advisors.length);
        //当前类的class
        Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
        //遍历所有aop通知，找到符合当前方法的aop通知
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                // Add it conditionally.
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                // 先校验当前Advisor是否适用于当前对象
                if (pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    boolean match;
                    // 再校验Advisor是否应用到当前方法上
                    if (mm.matches(method, actualClass)) {
                        MethodInterceptor interceptor = (MethodInterceptor) advisor.getAdvice();
                        interceptorList.add(interceptor);
                    }
                }
            }
        }
        return interceptorList;
    }
}
