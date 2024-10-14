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
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        //1.原始方式：一个一个mapper注入到ioc容器
//        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition()
//        .getBeanDefinition();
//        beanDefinition.setBeanClass(MapperFactoryBean.class);
//        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper.class);
//        registry.registerBeanDefinition("userMapper", beanDefinition);

        //2.改进方案：使用一个注解也即@MapperScan来一次性扫描和注册！
        //获取MapperScan注解中的属性value值，必须使用这种方式传入注解名称，而不能直接传maperScan！！！
        //注意：AnnotationMetadata就是封装了启动类上所有的注解信息，
        //也即把springboot启动类上所有的注解信息全部封装好，随时调用，极其方便！
        //而并不只是某个注解上的注解信息！
        Map<String, Object> annotationAttributes =
            importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName());
        //获取mapper所在的包名
        String packageName = (String) annotationAttributes.get("value");
        //获取mapper扫描器
        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        //开启扫描！
        classPathMapperScanner.doScan(packageName);
    }
}
