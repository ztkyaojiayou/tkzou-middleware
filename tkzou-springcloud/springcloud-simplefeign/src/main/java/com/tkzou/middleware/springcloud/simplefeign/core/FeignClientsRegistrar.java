package com.tkzou.middleware.springcloud.simplefeign.core;

import cn.hutool.core.util.ClassUtil;
import com.tkzou.middleware.springcloud.simplefeign.annotation.FeignClient;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * 往bean容器中注册Feign客户端
 * 用于解析@FeignClient注解，将对应的接口生成feign型的代理对象
 * 该代理对象就可以直接集成ribbon负载均衡发起请求啦！
 *
 * @author zoutongkun
 * @date 2022/4/7
 */
public class FeignClientsRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 往bean容器中注册Feign客户端
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //1.扫描所有带@FeignClient注解的接口，将其生成beanDefinition注册到ioc容器，以便于生成bean！
        // 为FeignClient注解修饰的接口生成代理bean即Feign客户端，并注册到bean容器
        //这里对于扫包的逻辑就是扫描EnableFeignClients注解所在的包包及其子包，
        //因此一般就需要加载springboot启动类上，和springboot的扫描路径一致！
        String packageName = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        //扫描所有被FeignClient注解修饰的接口，统一都是设置为FeignClientFactoryBean，也即FactoryBean
        //再在对应的getObject方法中通过较为复杂的方式生成bean，比如动态代理等，参考mybatis中的mapper接口的bean实现！！！
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(packageName, FeignClient.class);
        //2.遍历，一个一个注册
        for (Class<?> clazz : classes) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            // 设置bean的类型为FeignClientFactoryBean
            // 使用FeignClientFactoryBean生成Feign客户端
            // 最终就是在FeignClientFactoryBean的getObject方法中生成bean
            beanDefinition.setBeanClass(FeignClientFactoryBean.class);
            String clientName = clazz.getAnnotation(FeignClient.class).value();
            beanDefinition.getPropertyValues().addPropertyValue("contextId", clientName);
            beanDefinition.getPropertyValues().addPropertyValue("type", clazz);

            //将Feign客户端注册进bean容器
            String beanName = clazz.getName();
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }
}
