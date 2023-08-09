package com.tkzou.middleware.spring;

/**
 * 自定义bean相关的异常
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:18
 */
public class BeansException extends RuntimeException {

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
