package com.tkzou.middleware.doublecache.core.aspect;

import com.tkzou.middleware.doublecache.core.annotation.CacheAdd;
import com.tkzou.middleware.doublecache.core.annotation.CacheDelete;
import com.tkzou.middleware.doublecache.core.annotation.CacheUpdate;
import com.tkzou.middleware.doublecache.config.DoubleCacheConfig;
import com.tkzou.middleware.doublecache.core.cache.DoubleCacheService;
import com.tkzou.middleware.doublecache.utils.CacheUtil;
import com.tkzou.middleware.doublecache.utils.KeyGenerators;
import com.tkzou.middleware.doublecache.utils.SpElUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 二级缓存AOP切面
 * 整合二级缓存逻辑，非常简单！
 *
 * @author zoutongkun
 */
@Aspect
@Order(101)
@Component
@Slf4j
@ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
public class DoubleCacheManagerAspect {

    @Autowired
    SpElUtil spElUtil;

    @Autowired
    DoubleCacheService doubleCacheService;

    @Autowired
    private DoubleCacheConfig doubleCacheConfig;

    /**
     * 获取数据时
     */
    @Pointcut("execution(* com.tkzou..*.*(..)) && @annotation(com.tkzou.middleware.doublecache.core.annotation.CacheAdd)")
    public void executionOfCacheAddMethod() {
    }

    /**
     * 更新数据时
     */
    @Pointcut("execution(* com.tkzou..*.*(..)) && @annotation(com.tkzou.middleware.doublecache.core.annotation.CacheUpdate)")
    public void executionOfCacheUpdateMethod() {
    }

    /**
     * 删除数据时
     */
    @Pointcut("execution(* com.tkzou..*.*(..))  && @annotation(com.tkzou.middleware.doublecache.core.annotation.CacheDelete)")
    public void executionOfCacheDeleteMethod() {
    }

    @AfterReturning(pointcut = "executionOfCacheUpdateMethod()", returning = "returnObject")
    public void UpdateCache(final JoinPoint joinPoint, final Object returnObject) {

        try {
            if (returnObject == null) {
                return;
            }

            if (!doubleCacheConfig.isEnableCache()) {
                return;
            }
            CacheUpdate cacheUpdateAnnotation = getAnnotation(joinPoint, CacheUpdate.class);
            Object cacheKey = spElUtil
                    .parseAndGetCacheKeyFromExpression(cacheUpdateAnnotation.keyExpression(), returnObject,
                            joinPoint.getArgs(), cacheUpdateAnnotation.keyGenerator());

            if (cacheUpdateAnnotation.isAsync()) {
                doubleCacheService.saveByAsync(cacheUpdateAnnotation.cacheNames(), cacheKey, returnObject, cacheUpdateAnnotation.TTL());
            } else {
                doubleCacheService.save(cacheUpdateAnnotation.cacheNames(), cacheKey, returnObject, cacheUpdateAnnotation.TTL());
            }

        } catch (Exception e) {
            log.error("putInCache # Data save failed ## " + e.getMessage(), e);
        }
    }

    @AfterReturning(pointcut = "executionOfCacheDeleteMethod()", returning = "returnObject")
    public void deleteCache(final JoinPoint joinPoint, final Object returnObject) {

        try {
            if (!doubleCacheConfig.isEnableCache()) {
                return;
            }
            CacheDelete cacheDeleteAnnotation = getAnnotation(joinPoint, CacheDelete.class);

            String[] cacheNames = cacheDeleteAnnotation.cacheNames();
            Object cacheKey = null;
            if (!cacheDeleteAnnotation.removeAll()) {
                cacheKey = spElUtil
                        .parseAndGetCacheKeyFromExpression(cacheDeleteAnnotation.keyExpression(), returnObject,
                                joinPoint.getArgs(), cacheDeleteAnnotation.keyGenerator());
            }
            if (cacheDeleteAnnotation.isAsync()) {
                if (cacheDeleteAnnotation.removeAll()) {
                    doubleCacheService.delete(cacheNames);
                }
            } else {
                doubleCacheService.delete(cacheNames, cacheKey);
            }

        } catch (Exception e) {
            log.error("putInCache # Data delete failed! ## " + e.getMessage(), e);
        }
    }

    @Around("executionOfCacheAddMethod()")
    public Object getAndAddCache(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //未开启缓存时，直接执行原方法
        if (!doubleCacheConfig.isEnableCache()) {
            return callActualMethod(proceedingJoinPoint);
        }

        Object returnObject = null;

        CacheAdd cacheAddAnnotation = null;
        Object cacheKey = null;
        try {
            cacheAddAnnotation = getAnnotation(proceedingJoinPoint, CacheAdd.class);
            KeyGenerators keyGenerator = cacheAddAnnotation.keyGenerator();
            if (StringUtils.isEmpty(cacheAddAnnotation.keyExpression())) {
                //构建缓存key
                cacheKey = CacheUtil.buildCacheKey(proceedingJoinPoint.getArgs());
            } else {
                cacheKey = spElUtil
                        .parseAndGetCacheKeyFromExpression(cacheAddAnnotation.keyExpression(), null,
                                proceedingJoinPoint.getArgs(), keyGenerator);
            }
            //从缓存中获取数据
            //包括两级缓存
            returnObject = doubleCacheService.get(cacheAddAnnotation.cacheName(), cacheKey);

        } catch (Exception e) {
            log.error("getAndSaveInCache # Redis op Exception while trying to get from cache ## " + e.getMessage(), e);
        }
        //若缓存中有，则直接返回
        if (returnObject != null) {
            return returnObject;
        } else {
            //否则，调用原方法，一般就是从数据库中获取！
            returnObject = callActualMethod(proceedingJoinPoint);
            //再写回到缓存（先写redis，再写本地缓存！）
            if (returnObject != null) {
                try {
                    assert cacheAddAnnotation != null;
                    //是否异步写入
                    if (cacheAddAnnotation.isAsync()) {
                        doubleCacheService
                                .saveByAsync(new String[]{cacheAddAnnotation.cacheName()}, cacheKey,
                                        returnObject, cacheAddAnnotation.TTL());
                    } else {
                        doubleCacheService
                                .save(new String[]{cacheAddAnnotation.cacheName()}, cacheKey,
                                        returnObject, cacheAddAnnotation.TTL());
                    }
                } catch (Exception e) {
                    log.error("getAndSaveInCache # Exception occurred while trying to save data in redis##" + e.getMessage(),
                            e);
                }
            }
        }
        return returnObject;
    }

    /**
     * 执行目标方法，相当于是直接从数据库取
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    private Object callActualMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed();

    }

    private <T extends Annotation> T getAnnotation(JoinPoint proceedingJoinPoint,
                                                   Class<T> annotationClass) throws NoSuchMethodException {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        if (method.getDeclaringClass().isInterface()) {
            method = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(methodName,
                    method.getParameterTypes());
        }
        return method.getAnnotation(annotationClass);
    }

}
