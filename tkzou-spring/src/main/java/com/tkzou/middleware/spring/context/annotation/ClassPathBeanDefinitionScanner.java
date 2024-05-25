package com.tkzou.middleware.spring.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.support.BeanDefinitionRegistry;
import com.tkzou.middleware.spring.stereotype.Component;

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
