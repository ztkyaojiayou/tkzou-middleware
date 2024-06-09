package com.tkzou.middleware.rpc.framework.register;

import cn.hutool.core.collection.CollectionUtil;
import com.tkzou.middleware.rpc.framework.protocol.ProtocolFactory;
import com.tkzou.middleware.rpc.framework.consumer.MethodInvoker;
import com.tkzou.middleware.rpc.framework.protocol.RpcProtocol;
import com.tkzou.middleware.rpc.framework.protocol.ServiceInstance;

import java.io.*;
import java.util.*;

/**
 * 模拟服务注册中心
 * 包括服务的注册和发现
 *
 * @author zoutongkun
 */
public class RemoteServiceRegister {
    /**
     * 服务信息
     * 包含具体的实现类！
     */
    private static Map<String, List<ServiceInstance>> SERVICE_REGISTER = new HashMap<>();

    /**
     * 注册服务
     *
     * @param interfaceName   接口名，也可以理解为服务名
     * @param serviceInstance
     */
    public static void register(String interfaceName, ServiceInstance serviceInstance) {
        List<ServiceInstance> serviceInstances = SERVICE_REGISTER.get(interfaceName);
        if (serviceInstances == null) {
            serviceInstances = new ArrayList<>();
        }
        serviceInstances.add(serviceInstance);
        SERVICE_REGISTER.put(interfaceName, serviceInstances);
        //当前因为没有使用独立的注册中心，
        //就先保存在文件目录中以模拟实际的注册中心的数据共享功能！
        saveConfigToFile();
    }

    /**
     * 从注册中心获取/发现服务实例列表
     *
     * @param interfaceName
     * @return
     */
    public static List<ServiceInstance> get(String interfaceName) {
        SERVICE_REGISTER = getConfigFromFile();
        return SERVICE_REGISTER.get(interfaceName);
    }

    /**
     * 从注册中心获取所有的服务实例，最终封装为MethodInvoker
     *
     * @param interfaceClass
     * @return
     */
    public static List<MethodInvoker> getCandidateMethodInvokers(Class interfaceClass) {
        List<MethodInvoker> invokerList = new ArrayList<>();
        List<ServiceInstance> serviceInstanceList = RemoteServiceRegister.get(interfaceClass.getName());
        if (CollectionUtil.isEmpty(serviceInstanceList)) {
            return Collections.emptyList();
        }
        //解析为MethodInvoker
        serviceInstanceList.forEach(serviceInstance -> {
            RpcProtocol rpcProtocol = ProtocolFactory.getRpcProtocol(serviceInstance.getProtocol());
            MethodInvoker methodInvoker = rpcProtocol.refer(serviceInstance);
            invokerList.add(methodInvoker);
        });
        return invokerList;
    }

    /**
     * 将配置信息保存到文件系统
     * 一般而言是单独启动一个web服务来存储的！
     */
    private static void saveConfigToFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/temp.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(SERVICE_REGISTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中取配置文件
     * 相当于去独立的配置中心获取配置文件
     *
     * @return
     */
    private static Map<String, List<ServiceInstance>> getConfigFromFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("/temp.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Map<String, List<ServiceInstance>>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
