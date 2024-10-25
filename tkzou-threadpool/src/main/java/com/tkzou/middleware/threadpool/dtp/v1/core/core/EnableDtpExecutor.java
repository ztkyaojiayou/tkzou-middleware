package com.tkzou.middleware.threadpool.dtp.v1.core.core;

import com.tkzou.middleware.threadpool.dtp.v1.core.spring.DtpImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启动态线程池功能
 * 用在普通线程池上，使之成为动态线程池
 *
 * @author zoutongkun
 * @Date 2023/5/19 23:48
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DtpImportSelector.class)
public @interface EnableDtpExecutor {
}
