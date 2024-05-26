package com.tkzou.middleware.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.tkzou.middleware.springframework.stereotype.Component;

import java.util.Set;

/**
 * 类路径bean组件扫描器
 *
 * @author :zoutongkun
 * @date :2024/5/25 4:33 下午
 * @description :
 * @modyified By:
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation" +
            ".internalAutowiredAnnotationProcessor";

    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 扫描指定包路径下带@Component注解的类
     * 将其封装为BeanDefinition，
     * 再注册到spring的beanDefinitionMap中备用，
     * 这其实是整个spring的核心！！！
     *
     * @param basePackages
     */
    public void doScan(String... basePackages) {
        //1.扫描指定包路径下带@Component注解的类，
        // 生成对应的BeanDefinition注册到beanDefinitionMap中
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateBeanDefinitions = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidateBeanDefinitions) {
                // 解析bean的作用域
                String beanScope = resolveBeanScope(candidate);
                if (StrUtil.isNotEmpty(beanScope)) {
                    candidate.setScope(beanScope);
                }
                //生成bean的名称
                String beanName = determineBeanName(candidate);
                //注册BeanDefinition
                registry.registerBeanDefinition(beanName, candidate);
            }
        }

        //2.注册处理@Autowired和@Value注解的BeanPostProcessor，
        //也即AutowiredAnnotationBeanPostProcessor
        //也即用于初始化该bean，这个bean名称直接写死，这种手法在spring中常用！
        // 因为有特点含义，且是spring自己的bean。
        registry.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
                new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    /**
     * 获取bean的作用域
     * 默认单例
     *
     * @param beanDefinition
     * @return
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope != null) {
            return scope.value();
        }

        return StrUtil.EMPTY;
    }


    /**
     * 生成bean的名称
     *
     * @param beanDefinition
     * @return
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }


}
