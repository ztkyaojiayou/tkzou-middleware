package com.tkzou.middleware.spring.aop.framework;

import com.tkzou.middleware.spring.aop.AdvisedSupport;

/**
 * @author :zoutongkun
 * @date :2024/5/24 10:50 下午
 * @description :
 * @modyified By:
 */
public class ProxyFactory {
    private AdvisedSupport advisedSupport;

    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    /**
     * 根据代理类型生成代理对象
     *
     * @return
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        // 根据代理类型判断使用哪种代理方式
        if (advisedSupport.isProxyTargetClass()) {
            return new CglibAopProxy(advisedSupport);
        }

        return new JdkDynamicAopProxy(advisedSupport);
    }

}
