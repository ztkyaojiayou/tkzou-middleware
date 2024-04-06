package com.tkzou.middleware.springboot.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动自动配置
 *
 * @author zoutongkun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
}
