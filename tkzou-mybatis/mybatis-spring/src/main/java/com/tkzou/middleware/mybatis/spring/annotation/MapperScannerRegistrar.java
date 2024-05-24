package com.tkzou.middleware.mybatis.spring.annotation;

import com.tkzou.middleware.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * <p> 自定义注册BeanDefinition </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 05:03
 */
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
//        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
//        beanDefinition.setBeanClass(MapperFactoryBean.class);
//        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);
//        registry.registerBeanDefinition("userMapper", beanDefinition);
//获取MapperScan注解中的属性value值，必须使用这种方式传入注解名称，而不能直接传maperScan！！！
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName());
        String packageName = (String) annotationAttributes.get("value");

        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        classPathMapperScanner.doScan(packageName);
    }
}
