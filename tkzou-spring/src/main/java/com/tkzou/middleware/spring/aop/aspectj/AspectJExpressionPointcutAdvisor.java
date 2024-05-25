package com.tkzou.middleware.spring.aop.aspectj;

import com.tkzou.middleware.spring.aop.Pointcut;
import com.tkzou.middleware.spring.aop.PointcutAdvisor;
import org.aopalliance.aop.Advice;

/**
 * 切面
 * 切入点+通知的具体实现，该类就可以理解为一个切面了！
 * 切面 = 既有切入点，又有通知逻辑
 * 比如@Aspect注解所定义的类，就是一个切面！
 * 在源码中，它也是一个最终的实现类，
 * 但定义了一些相关的抽象类来进一步封装、细化和解耦，
 * 本质相同，理解即可。
 *
 * @author :zoutongkun
 * @date :2024/5/25 1:11 下午
 * @description :
 * @modyified By:
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    /**
     * 需要传入的切入点表达式，
     * 比如：execution(* com.tkzou.middleware.spring.service.*.*(..))
     * 一般是通过配置文件或者注解传入！！！
     */
    private String expression;
    /**
     * 由切入点表达式解析得到的切点，
     * 之后就可以通过它来直接判断目标方法是否符合该切入点表达式啦！
     */
    private AspectJExpressionPointcut pointcut;
    /**
     * 通知，也即目标方法被切中后需要执行的增强逻辑！
     * 也即拦截器
     */
    private Advice advice;

    public AspectJExpressionPointcutAdvisor() {
    }

    /**
     * 构造器
     *
     * @param expression
     * @param advice
     */
    public AspectJExpressionPointcutAdvisor(String expression, Advice advice) {
        this.expression = expression;
        this.advice = advice;
    }

    public AspectJExpressionPointcutAdvisor(String expression) {
        this.expression = expression;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Pointcut getPointcut() {
        if (pointcut == null) {
            //解析传入的切入点表达式备用！
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }
}
