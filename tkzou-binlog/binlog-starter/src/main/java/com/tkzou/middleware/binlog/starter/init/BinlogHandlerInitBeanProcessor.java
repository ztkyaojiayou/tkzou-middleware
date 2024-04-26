package com.tkzou.middleware.binlog.starter.init;

import com.tkzou.middleware.binlog.core.IBinlogEventHandler;
import com.tkzou.middleware.binlog.core.client.BinlogClient;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.tkzou.middleware.binlog.starter.annotation.BinlogSubscriber;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

/**
 * 注册所有binlog处理器
 * 是个后置处理器，是springboot的一个扩展点，
 * 用于初始化！！！
 * 这里不好直接加一个@component，因为需要注入clientConfigs，
 * 而它是map，因此就没有直接在这里注入了，
 * 而是在BinlogAutoConfiguration类中单独以@Bean的形式注入这个对象啦！！！
 *
 * @author zoutongkun
 */
public class BinlogHandlerInitBeanProcessor implements SmartInitializingSingleton, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, BinlogClientConfig> clientConfigs;

    public BinlogHandlerInitBeanProcessor(Map<String, BinlogClientConfig> clientConfigs) {
        this.clientConfigs = clientConfigs;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 初始化
     */
    @Override
    public void afterSingletonsInstantiated() {
        //1.从ioc容器中获取所有binlog处理器
        Map<String, IBinlogEventHandler> handlers = applicationContext.getBeansOfType(IBinlogEventHandler.class);
        //或者直接使用如下api获取带有指定注解的bean
//        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(BinlogSubscriber.class);
        clientConfigs.forEach((clientName, clientConfig) -> {
            //2.注册所有binlog处理器
            BinlogClient client = BinlogClient.create(clientConfig);
            handlers.forEach((beanName, handler) -> {
                //2.1只有带有@BinlogSubscriber注解的才是真正的binlog处理器
                BinlogSubscriber annotation = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(handler),
                        BinlogSubscriber.class);
                if (annotation != null) {
                    //2.2注册到对应服务实例上
                    if (clientName.equals(annotation.clientName())) {
                        client.registerEventHandler(handler);
                    }
                }
            });
            client.connect();
        });
    }
}
