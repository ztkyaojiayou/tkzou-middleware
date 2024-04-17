package com.tkzou.middleware.configcenter.client.client.listener;

import com.tkzou.middleware.configcenter.client.core.ConfigService;
import com.tkzou.middleware.configcenter.client.client.domain.ConfigFile;
import com.tkzou.middleware.configcenter.client.config.ConfigCenterConfig;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;

/**
 * 自动刷新配置
 * 逻辑：就是发布一个RefreshEvent事件，
 * 之后就会有对应的监听者去更新对应bean的属性！
 *
 * @author zoutongkun
 * @date 2022/9/30 12:49
 */
public class ConfigContextRefresher implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    @Resource
    private ConfigCenterConfig configCenterConfig;

    @Resource
    private ConfigService configService;

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //注册监听器，属于项目的初始化准备工作！！！真正触发是在实例化ConfigService对象时，
        //它会同时启动一个定时任务去拉取配置中心中的最新值并和本地原值进行比对，
        //若发现有更新，则会触发对应的事件，以告知spring-cloud来动态刷新对应bean的属性值！！！

        //我们是通过监听ApplicationReadyEvent事件来注册各个配置文件的监听器，
        //此时，ioc容器已经加载完成。
        //ApplicationReadyEvent 的调用点是 listeners.running(context);
        registerListeners();
    }

    /**
     * 对配置文件注册对应的监听器
     * 是对每一个配置文件都要设置一个监听器
     * 这里的fileId指的是整个配置文件，如xxx.properties，其实就是ConfigFile中的fileId
     */
    private void registerListeners() {
        configService.addListener(configCenterConfig.getConfigFileId(), new ConfigFileChangedListener() {
            @Override
            public void onFileChanged(ConfigFile configFile) {
                //发布RefreshEvent事件，该事件是一个刷新配置文件的事件，是spring-cloud提供的扩展点，
                //当发布该事件后，springboot就会自动从配置中心拉取数据，修改Bean的属性
                //触发时机：当配置文件有更新时！！！
                //参考：https://blog.csdn.net/JokerLJG/article/details/120254643
                //https://blog.csdn.net/m0_71777195/article/details/126319418
                applicationContext.publishEvent(new RefreshEvent(this, null, "Refresh zoutongkun config"));
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
