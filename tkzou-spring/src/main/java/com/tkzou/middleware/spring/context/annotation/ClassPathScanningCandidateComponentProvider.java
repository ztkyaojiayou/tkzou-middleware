package com.tkzou.middleware.spring.context.annotation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import com.google.common.collect.Sets;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.stereotype.Component;
import com.tkzou.middleware.spring.stereotype.Controller;
import com.tkzou.middleware.spring.stereotype.Repository;
import com.tkzou.middleware.spring.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * bean组件扫描器，是个父类，核心子类是ClassPathBeanDefinitionScanner
 * 用于扫描指定包下的所有带有@Component注解的类，
 * 并将其封装为BeanDefinition
 *
 * @author :zoutongkun
 * @date :2024/5/25 4:33 下午
 * @description :
 * @modyified By:
 */
public class ClassPathScanningCandidateComponentProvider {
    /**
     * 需要扫描的bean注解类型
     */
    private static final Set<Class<?>> candidateComponentTypes = new HashSet<>();

    static {
        candidateComponentTypes.addAll(Sets.newHashSet(Component.class, Controller.class, Repository.class,
                Service.class));
    }

    /**
     * 扫描出指定包下的所有带@Component注解的类，
     * 并将其封装为BeanDefinition，这其实是整个spring的核心！！！
     *
     * @param basePackage 包路径，如"com.tkzou.middleware.spring"。
     * @return
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        // 扫描所有带有@Component注解的类
        //todo 可能扫描不到它的衍生注解，若不行，则可以先扫描出所有class，再筛选！
        Set<Class<?>> componentClasses = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        //扫描出所有目标bean，兼容一下
        Set<Class<?>> allClass = ClassUtil.scanPackage(basePackage, candidateComponentTypes::contains);
        componentClasses.addAll(allClass);
        for (Class<?> clazz : componentClasses) {
            //组装成BeanDefinition
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            candidates.add(beanDefinition);
        }
        return candidates;
    }
}
