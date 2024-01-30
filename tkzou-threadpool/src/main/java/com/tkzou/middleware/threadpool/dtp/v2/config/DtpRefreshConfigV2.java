package com.tkzou.middleware.threadpool.dtp.v2.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.tkzou.middleware.threadpool.dtp.v2.DtpExecutorFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 配置nacos动态配置监听器-核心关键
 * 这里通过读取properties后缀的文件获取
 * 这里使用了InitialzingBean接口，即spring提供的bean初始化回调接口，
 * 在spring初始化完成这个bean后，如果我们的组件实现了这个接口spring就会回调afterPropertiesSet()这个方法来对我们的bean做增强
 * 逻辑也很简单，大家自己看代码即可，我们在nacos动态刷新配置后，回调接口
 * <p>
 * 参考：https://blog.csdn.net/m0_57334678/article/details/131466456
 *
 * @author zoutongkun
 */
@Configuration
public class DtpRefreshConfigV2 implements InitializingBean {
    /**
     * 实现InitializingBean接口在初始化类后会回调afterProperties方法来增强bean
     */
    @Resource
    private NacosConfigManager nacosConfigManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        //1.获取nacos配置文件处理器
        ConfigService nacosConfigService = nacosConfigManager.getConfigService();
        //2.添加指定配置文件的监听器，读取nacos上的配置文件
        nacosConfigService.addListener("dtpExecutor-dev.properties", "DEFAULT_GROUP", new Listener() {
            @Override
            public Executor getExecutor() {
                //返回一个线程池
                return Executors.newSingleThreadExecutor();
            }

            /**
             * 接收配置文件中的数据--核心
             * @param s 字符流，而非配置文件的json，需要先解析！！！
             */
            @Override
            public void receiveConfigInfo(String s) {
                //读取配置文件并刷新
                refreshConfig(s);
            }
        });
    }

    /**
     * 刷新配置文件信息
     *
     * @param config
     */
    private void refreshConfig(String config) {
        Properties threadProperties = new Properties();
        try {
            //解析/加载/读取配置文件
            threadProperties.load(new StringReader(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<Object> configSet = threadProperties.keySet();
        Map<String, Integer> configMap = new HashMap<>();
        for (Object curKey : configSet) {
            //即如：spring.datasource.username
            String configString = curKey.toString();
            //再按照句号切割并转为数组
            String[] configNums = configString.split("\\.");
            //线程池id
            String threadPoolId = configNums[2];
            //配置的参数，如coreSize
            String configType = configNums[3];
            //将线程池id与参数类型作为key，可以防止配置到其他线程池中去
            String configKey = threadPoolId + configType;
            //获取该key的值
            String configPropertiesValue = threadProperties.getProperty(curKey.toString());

            //保存起来
            configMap.put(configKey, Integer.valueOf(configPropertiesValue));
            System.out.println("修改线程池参数：" + configKey + "为" + configPropertiesValue);
        }
        //刷新到线程池配置！
        DtpExecutorFactory.refreshDtpExecutorConfig(configMap);
    }
}