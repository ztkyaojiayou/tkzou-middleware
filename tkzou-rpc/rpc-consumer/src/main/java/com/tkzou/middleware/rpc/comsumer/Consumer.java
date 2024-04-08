package com.tkzou.middleware.rpc.comsumer;

import com.tkzou.middleware.rpc.framework.ProxyFactory;
import com.tkzou.middleware.rpc.provider.api.HelloService;

/**
 * 服务消费者
 *
 * @author zoutongkun
 */
public class Consumer {

    public static void main(String[] args) {
        //获取一个目标类的代理对象
        //注意：在实际的rpc框架中，这一步也会由框架自动完成，我们只需通过一个注解注入即可！！！
        //在feign中，就是使用@Autowired即可，此时spring就会为我们注入这个代理对象！！！
        //也因此，在feign中，首先会在ioc容器初始化时通过spring提供的扩展点来初始化代理对象！！！
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        //通过这个代理对象来执行方法
        String result = helloService.sayHello("tkzou-rpc");
        System.out.println(result);
    }
}
