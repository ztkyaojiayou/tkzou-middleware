package com.tkzou.middleware.dynamicdb.aspect;

import com.tkzou.middleware.dynamicdb.annotation.DataSource;
import com.tkzou.middleware.dynamicdb.core.DynamicDataSourceHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 数据源的动态切换切面
 * 这里务必注意一点：那就是和 @Transactional注解共用时的顺序问题，
 * 默认情况下，在切换数据源之前解析@Transactional的切面先执行，此时会去获取数据源，
 * 而此时数据源还没有切换就会获取默认的数据源，
 * 在执行完@Transactional对应的切面后，此时也就已经拿到数据库连接了，
 * 此时再执行解析@DataSource("mysql")的切面，
 * 易知，此时切换数据源的时候只是改变了缓存数据源配置的key字符串，
 * 在执行db操作的时候并没有重新根据当前字符串的key去获取最新的数据源，
 * 这样就无法正常切换数据源了！！！
 * 解决方案：可以设置@Order(-1)来执行方法上的注解的执行顺序，可以优先于@Transactional执行。
 * 参考：https://mp.weixin.qq.com/s/bfSLFlC8UPthENWiVBxeaw
 *
 * @Author: zoutongkun
 * @CreateDate: 2024/5/17 14:03
 */
@Aspect
@Order(-1)  //保证该AOP在@Transactional之前执行，否则需要单独考虑事务问题，这很重要！
@Component
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.tkzou.middleware.dynamicdb.annotation.DataSource)")
    public void dynamicDataSourcePointCut() {
    }

    @Around("dynamicDataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String tarDataSourceName = getTarDataSourceName(joinPoint).value();
        //先设置一下当前方法要使用的数据源名称
        DynamicDataSourceHolder.setKey(tarDataSourceName);
        try {
            //执行目标方法，此时mybatis会根据当前设置的数据源名称找到对应的数据源并获取连接
            return joinPoint.proceed();
        } finally {
            //执行完后记得清除一下
            DynamicDataSourceHolder.clear();
        }
    }

    /**
     * 获取当前方法上配置的数据源信息
     * 先判断方法的注解，后判断类的注解，以方法的注解优先
     *
     * @param joinPoint
     * @return
     */
    private DataSource getTarDataSourceName(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DataSource dataSourceAnnotation = methodSignature.getMethod().getAnnotation(DataSource.class);
        if (Objects.nonNull(dataSourceAnnotation)) {
            return dataSourceAnnotation;
        } else {
            Class<?> dsClass = joinPoint.getTarget().getClass();
            return dsClass.getAnnotation(DataSource.class);
        }
    }
}
