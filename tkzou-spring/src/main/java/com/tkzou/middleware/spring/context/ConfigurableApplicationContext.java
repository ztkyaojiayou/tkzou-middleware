package com.tkzou.middleware.spring.context;

import com.tkzou.middleware.spring.beans.BeansException;

/**
 * 可配置的应用上下文
 *
 * @author :zoutongkun
 * @date :2023/8/28 11:28 下午
 * @description :
 * @modyified By:
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 最著名的方法：刷新容器！！！
     * @throws BeansException
     */
    void refresh() throws BeansException;
}
