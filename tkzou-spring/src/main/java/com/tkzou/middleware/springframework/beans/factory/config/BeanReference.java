package com.tkzou.middleware.springframework.beans.factory.config;

/**
 * 一个bean对另一个bean的引用
 * 在创建bean实例时会用到
 * 如beanA依赖beanB，则需先实例化beanB
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 14:54
 */
public class BeanReference {
    /**
     * bean名称
     * 在实例化时，若当前要实例化的bean依赖该bean，则会先去容器中找该bean对象，
     * 而若没有，则会先实例化该bean并存入容器，再注给当前bean，以便顺利实例化
     * todo 注意：由于不想增加代码的复杂度和理解难度，暂时不⽀持循环依赖，后续再议！
     */
    private final String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
