package com.tkzou.middleware.springcloud.simplefeign.core;

import feign.Contract;
import feign.MethodMetadata;
import feign.Request;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * feign支持Spring MVC的注解所需的核心类
 * 最终极速通过该类去解析接口上各个方法的，
 * 如方法入参、方法出参、方法传入参类型等！
 * 而无需由我们去维护啦！
 *
 * @author zoutongkun
 * @date 2024/4/29
 */
public class SpringMvcContract extends Contract.BaseContract {
    /**
     * 解析接口上的注解
     *
     * @param data
     * @param clz
     */
    @Override
    protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
        //TODO 解析接口注解
    }

    /**
     * 解析方法上的注解
     *
     * @param data
     * @param annotation
     * @param method
     */
    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
        //解析方法注解
        //解析PostMapping注解
        if (annotation instanceof PostMapping) {
            PostMapping postMapping = (PostMapping) annotation;
            data.template().method(Request.HttpMethod.POST);
            String path = postMapping.value()[0];
            if (!path.startsWith("/") && !data.template().path().endsWith("/")) {
                path = "/" + path;
            }
            data.template().uri(path, true);
        }

        //TODO 解析其他注解
    }

    /**
     * 解析方法参数上的注解
     *
     * @param data
     * @param annotations
     * @param paramIndex
     * @return
     */
    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        //TODO 解析参数
        return true;
    }
}
