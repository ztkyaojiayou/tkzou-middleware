package com.tkzou.middleware.mybatis.spring.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> mapper包扫描 </p>
 * spring不会自动扫描带@Import的注解，而需要和@configuration注解一起使用才生效！
 * 因此要想spring能扫描我们自定义的该注解，就要求当前注解上加上@configuration注解，亲测！
 * 另外，@Bean就必须与@Configuration或@Component等注解一起使用，因为@Bean本身不表示一个bean。
 * 同时，只有和@Configuration一起使用时，@Bean注入的bean才是单例的！！！
 * 参考：https://blog.csdn.net/AwayFuture/article/details/105845005
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 05:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MapperScannerRegistrar.class)
@Configuration
public @interface MapperScan {
    /**
     * mapper接口所在的包路径
     *
     * @return
     */
    String value();

}
