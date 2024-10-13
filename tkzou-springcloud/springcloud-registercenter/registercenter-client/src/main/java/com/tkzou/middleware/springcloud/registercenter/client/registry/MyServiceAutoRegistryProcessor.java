package com.tkzou.middleware.springcloud.registercenter.client.registry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * 服务自动注册处理器，本质是个事件监听器
 * 具体是如何实现的？
 * 就是监听springboot发布的WebServerInitializedEvent事件
 * 具体是由AbstractAutoServiceRegistration监听并调用的！
 * 然后依次调用onApplicationEvent->bind->start->register()
 * 最终就会调用MyServiceRegistry的register方法！！！
 * 这就连起来啦！
 *
 * @author zoutongkun
 * @date 2024/4/28
 */
public class MyServiceAutoRegistryProcessor extends AbstractAutoServiceRegistration<Registration> {

    private MyRegistration myRegistration;

    public MyServiceAutoRegistryProcessor(ServiceRegistry<Registration> serviceRegistry,
                                          MyRegistration myRegistration) {
        super(serviceRegistry, null);
        this.myRegistration = myRegistration;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Registration getRegistration() {
        if (myRegistration.getPort() < 0) {
            //设置服务端口
            myRegistration.setPort(this.getPort().get());
        }
        return myRegistration;
    }

    @Override
    protected Object getConfiguration() {
        return myRegistration.getMyDiscoveryProperties();
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected Registration getManagementRegistration() {
        return null;
    }
}
