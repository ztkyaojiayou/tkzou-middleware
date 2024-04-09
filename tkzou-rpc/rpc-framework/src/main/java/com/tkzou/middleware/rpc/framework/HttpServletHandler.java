package com.tkzou.middleware.rpc.framework;

import com.tkzou.middleware.rpc.framework.protocol.MethodInvocation;
import com.tkzou.middleware.rpc.framework.protocol.ServletHandler;
import com.tkzou.middleware.rpc.framework.register.LocalRegister;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

/**
 * 具体的http处理器
 *
 * @author zoutongkun
 */
public class HttpServletHandler implements ServletHandler {

    @Override
    public void handler(HttpServletRequest req, HttpServletResponse resp) {

        try {
            //1.获取服务消费者在请求体中发送的要调用的方法元消息methodInvocation
            MethodInvocation methodInvocation =
                    (MethodInvocation) new ObjectInputStream(req.getInputStream()).readObject();
            //接口名，可理解为服务名
            String interfaceName = methodInvocation.getInterfaceName();
            //2.从注册中心选一个具体的实现类（可理解为服务发现，即获取一个具体的服务实例来调用）
            Class implClass = LocalRegister.get(interfaceName);
            //3.通过反射机制执行该方法
            //3.1先获取该实现类中的该方法
            Method method = implClass.getMethod(methodInvocation.getMethodName(), methodInvocation.getParamType());
            //3.2再调用该方法
            String result = (String) method.invoke(implClass.newInstance(), methodInvocation.getParams());
            System.out.println("tomcat返回地方结果为:" + result);
            //4.最后返回该方法的返回值给服务消费者！
            IOUtils.write(result, resp.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
