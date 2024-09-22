package com.tkzou.middleware.sms.core.loadbalance;

import cn.hutool.core.bean.BeanUtil;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SmsLoad
 * <p> 自定义的一个平滑加权负载服务，可以用于存放多个短信实现负载
 *
 * @author :zoutongkun
 * 2024/4/21  20:49
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmsLoadBalancer {
    // 服务器列表，每个服务器有一个权重和当前权重
    private final List<SmsServer> smsServers = new ArrayList<>();

    private static final SmsLoadBalancer SMS_LOAD_BALANCER = new SmsLoadBalancer();

    /**
     * 单例
     * @return
     */
    public static SmsLoadBalancer getInstance() {
        return SMS_LOAD_BALANCER;
    }

    /**
     * addLoadServer
     * <p> 添加服务及其权重
     * <p>添加权重应注意，不要把某个权重设置的与其他权重相差过大，否则容易出现无法负载，单一选择的情况
     *
     * @param LoadServer 短信实现
     * @param weight     权重
     * @author :zoutongkun
     */
    public void addLoadServer(SmsClient LoadServer, int weight) {
        smsServers.add(SmsServer.create(LoadServer, weight, weight));
    }

    /**
     * 根据配置文件创建负载均衡器
     * <p> 创建smsBlend并加入到负载均衡器
     *
     * @param smsClient      短信服务
     * @param supplierConfig 厂商配置
     * @author :zoutongkun
     */
    public static void add(SmsClient smsClient, SmsProviderConfig supplierConfig) {
        Map<String, Object> supplierConfigMap = BeanUtil.beanToMap(supplierConfig);
        Object weight = supplierConfigMap.getOrDefault("weight", 1);
        SMS_LOAD_BALANCER.addLoadServer(smsClient, Integer.parseInt(weight.toString()));
    }

    /**
     * 根据配置文件创建负载均衡器
     * @param smsClient
     * @param weight
     */
    public static void add(SmsClient smsClient, int weight) {
        SMS_LOAD_BALANCER.addLoadServer(smsClient, weight);
    }

    /**
     * removeLoadServer
     * <p>移除短信服务
     *
     * @param LoadServer 要移除的服务
     * @author :zoutongkun
     */
    public void removeLoadServer(SmsClient LoadServer) {
        for (int i = 0; i < smsServers.size(); i++) {
            if (smsServers.get(i).getSmsClient().equals(LoadServer)) {
                smsServers.remove(i);
                break;
            }
        }
    }

    /**
     * getLoadServer
     * <p>根据负载算法获取一个可获取到的短信服务，这里获取到的服务必然是addLoadServer方法中添加过的服务，不会凭空出现
     *
     * @return SmsBlend 短信实现
     * @author :zoutongkun
     */
    public synchronized SmsClient getLoadServer() {
        int totalWeight = 0;
        SmsServer selectedSmsServer = null;
        // 计算所有服务器的权重总和，并选择当前权重最大的服务器
        for (SmsServer smsServer : smsServers) {
            totalWeight += smsServer.getWeight();
            int currentWeight = smsServer.getCurrentWeight() + smsServer.getWeight();
            smsServer.setCurrentWeight(currentWeight);
            if (selectedSmsServer == null || smsServer.getCurrentWeight() > selectedSmsServer.getCurrentWeight()) {
                selectedSmsServer = smsServer;
            }
        }
        // 如果没有服务器，则返回空
        if (selectedSmsServer == null) {
            return null;
        }
        // 更新选择的服务器的当前权重，并返回其地址
        int i = selectedSmsServer.getCurrentWeight() - totalWeight;
        selectedSmsServer.setCurrentWeight(i);
        return selectedSmsServer.getSmsClient();
    }

}

