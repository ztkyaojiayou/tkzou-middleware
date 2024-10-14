package com.tkzou.middleware.mybatis.spring.mapper;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Set;

/**
 * <p> 自定义扫描器 </p>
 *
 * @author tkzou.middleware.mybatis.coreya
 * @description 扫描指定包，发现并注册bean
 * @date 2024/5/5 05:46
 */
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
//        return super.isCandidateComponent(metadataReader); // 判断是否存在@Component注解
        return true;
    }

    /**
     * 判断是否为需要扫描的bean，这里就是简单判断一下是否为接口J即可
     * 因为我们约定，@mapperScanner注解配置的全是mapper接口！
     *
     * @param beanDefinition
     * @return
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface();
    }

    /**
     * 包扫描
     *
     * @param basePackages
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //1.执行父类的doScan方法，获取所有mapper接口
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        //将mapper接口一一注册成FactoryBean类型的BeanDefinition，具体就是MapperFactoryBean，非常重要！！！
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            AbstractBeanDefinition beanDefinition =
                (AbstractBeanDefinition) beanDefinitionHolder.getBeanDefinition();
            //1.添加构造函数的参数值，这里就是传入clazz对象，具体就是传入当前mapper的class!!!
            //注意：这一步必须放在前面，否则获取的就不是mapper的clazz而是MapperFactoryBean的clazz啦！！！
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
            //demo
//            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(UserMapper
//            .class);

            //2.修改为FactoryBean类型，便于代理mapper接口！
            //这样一来，spring在创建该类型的bean时就会调用FactoryBean的getObject方法，
            //而该方法在这里就是创建代理对象！！！
            beanDefinition.setBeanClass(MapperFactoryBean.class);
        }
        return beanDefinitionHolders;
    }
}
