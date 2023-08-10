package com.tkzou.middleware.spring.beans.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * bean容器/工厂
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 11:37
 */
public class SimpleBeanFactory {
    /**
     * bean容器，用于存放bean，本质就是个map
     * 其中的value存放对象
     */
    private Map<String, Object> beanMap = new HashMap<>();

    /**
     * 注册bean
     *
     * @param beanName
     * @param bean
     */
    public void registerBean(String beanName, Object bean) {
        beanMap.put(beanName, bean);
    }

    /**
     * 获取bean
     */
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }
}
