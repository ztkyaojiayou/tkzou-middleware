package com.tkzou.middleware.springframework.beans.factory;

import com.tkzou.middleware.springframework.beans.BeansException;

/**
 * 同理，实现该接口，能感知所属BeanFactory
 *
 * @author zoutongkun
 */
public interface BeanFactoryAware extends Aware {
    /**
     * 设置beanFactory到实现类中！
     *
     * @param beanFactory
     * @throws BeansException
     */
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
