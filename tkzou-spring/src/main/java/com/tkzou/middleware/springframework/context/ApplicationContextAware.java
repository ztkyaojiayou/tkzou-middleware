package com.tkzou.middleware.springframework.context;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.Aware;

/**
 * ApplicationContext感知接口
 * 实现该接口，能感知所属ApplicationContext
 *
 * @author zoutongkun
 */
public interface ApplicationContextAware extends Aware {
    /**
     * 设置所属ApplicationContext到当前类中
     * 此时applicationContext是被初始化好了的！
     *
     * @param applicationContext
     * @throws Exception
     */
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
