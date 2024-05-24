package com.tkzou.middleware.spring.aop.aspectj;

import com.tkzou.middleware.spring.aop.ClassFilter;
import com.tkzou.middleware.spring.aop.MethodMatcher;
import com.tkzou.middleware.spring.aop.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 切入点表达式解析和匹配器
 *
 * @author zoutongkun
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    private final static String DEFAULT_EXPRESSION = "execution(* *(..))";
    private final static String DEFAULT_EXPRESSION_WITH_ARGS = "execution(* *(*))";
    /**
     * 当前支持的切入点表达式
     * 当前仅支持execution表达式
     */
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    /**
     * 解析后的切入点表达式信息
     * 实现类为PointcutExpressionImpl
     */
    private final PointcutExpression pointcutExpression;

    /**
     * @param expression 目标切入点表达式
     */
    public AspectJExpressionPointcut(String expression) {
        //获取切入点表达式解析器
        PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());
        //解析传入的切入点表达式
        pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
