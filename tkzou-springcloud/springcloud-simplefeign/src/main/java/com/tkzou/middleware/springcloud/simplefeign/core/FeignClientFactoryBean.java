package com.tkzou.middleware.springcloud.simplefeign.core;

import com.tkzou.middleware.springcloud.simplefeign.context.FeignClientContextFactory;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 生成Feign客户端的FactoryBean
 * 最终就是在getObject()方法中生成bean
 *
 * @author zoutongkun
 * @date 2022/4/7
 */
public class FeignClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    private String contextId;
    /**
     * 当前bean的实际的class，
     * 是带有@FeignClient注解的类的class，
     * 而非xxxFactoryBean.class
     * 它是一个代理对象，里面就会封装好调用rpc的逻辑！！！
     */
    private Class<?> type;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() {
        //这里使用的bean都需要在自动配置类中注入！
        FeignClientContextFactory feignClientContextFactory =
            applicationContext.getBean(FeignClientContextFactory.class);
        Encoder encoder = feignClientContextFactory.getInstance(contextId, Encoder.class);
        Decoder decoder = feignClientContextFactory.getInstance(contextId, Decoder.class);
        Contract contract = feignClientContextFactory.getInstance(contextId, Contract.class);
        //用于调用rpc接口的httpClient！！！
        //这是就是LoadBalancerFeignClient
        Client client = feignClientContextFactory.getInstance(contextId, Client.class);
        //通过feign的builder去生成对应的bean
        //它是一个代理对象！！！
        //本质也是通过反射机制生成
        return Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .contract(contract)
            .client(client)
            .target(new HardCodedTarget<>(type, contextId, "http://" + contextId));
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
