package com.tkzou.middleware.springframework.aop.framework;

import com.tkzou.middleware.springframework.aop.AdvisedSupport;

/**
 * @author :zoutongkun
 * @date :2024/5/24 10:50 下午
 * @description :
 * @modyified By:
 */
public class ProxyFactory extends AdvisedSupport {

    public ProxyFactory() {
    }

    /**
     * 根据代理类型获取对应的代理方式
     *
     * @return
     */
    private AopProxy createAopProxy() {
        // 根据代理类型判断使用哪种代理方式
        if (this.isProxyTargetClass() || this.getTargetSource().getTargetClass().length == 0) {
            return new CglibAopProxy(this);
        }
        return new JdkDynamicAopProxy(this);
    }

    /**
     * 根据代理类型生成代理对象
     *
     * @return
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

}
