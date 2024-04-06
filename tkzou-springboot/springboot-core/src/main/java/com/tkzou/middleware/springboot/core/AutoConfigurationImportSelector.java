package com.tkzou.middleware.springboot.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 自动配置选择器，模拟通过spi机制从指定位置扫描bean
 * 如META-INF/spring.factories 或
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 *
 * @author zoutongkun
 */
public class AutoConfigurationImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 收集自动配置类
        // 通过java的spi机制将目标java文件转为clazz字节码对象
        // 此时需要将这些要注入的bean先放在resources的META-INF.services目录下，否则扫描不到，就无法将它们加载为clazz对象，那么之后就无法再通过反射机制为其生成对象/bean！！！
        ServiceLoader<AutoConfiguration> loader = ServiceLoader.load(AutoConfiguration.class);
        List<String> needToImportClazzList = new ArrayList<>();
        for (AutoConfiguration curConfig : loader) {
            needToImportClazzList.add(curConfig.getClass().getName());
        }

        return needToImportClazzList.toArray(new String[0]);
    }
}
