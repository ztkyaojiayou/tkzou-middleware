package com.tkzou.middleware.rpc.framework.register;

import cn.hutool.core.collection.CollectionUtil;
import com.tkzou.middleware.rpc.framework.ProtocolFactory;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvoker;
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
     */
    private static Map<String, List<ServiceInstance>> SERVICE_REGISTER = new HashMap<>();

    /**
     * 注册服务
     *
     * @param interfaceName
     * @param serviceInstance
     */
    public static void register(String interfaceName, ServiceInstance serviceInstance) {
        List<ServiceInstance> serviceInstances = SERVICE_REGISTER.get(interfaceName);
        if (serviceInstances == null) {
            serviceInstances = new ArrayList<>();
        }
        serviceInstances.add(serviceInstance);
        SERVICE_REGISTER.put(interfaceName, serviceInstances);
        //当前因为没有使用独立的注册中心，就先保存在文件目录中以模拟实际的注册中心的数据共享功能！！！
        saveFile();
    }

    /**
     * 从注册中心获取/发现服务实例列表
     *
     * @param interfaceName
     * @return
     */
    public static List<ServiceInstance> get(String interfaceName) {
        SERVICE_REGISTER = getFile();
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

    private static void saveFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/temp.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(SERVICE_REGISTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<ServiceInstance>> getFile() {
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
