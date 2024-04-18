package com.tkzou.middleware.configcenter.client.listener;

import com.tkzou.middleware.configcenter.client.config.ConfigCenterConfig;
import com.tkzou.middleware.configcenter.client.core.ConfigService;
import com.tkzou.middleware.configcenter.client.domain.ConfigFile;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;

/**
 * 自动刷新bean配置到ioc容器，此时ioc容器已经启动完毕了！
 * 此时就会为每一个从配置中心获取的配置文件添加一个配置文件更新的监听者，也即回调方法，也即ConfigFileChangedListener的一个实现，
 * <p>
 * 要注意的是，这个刷新是指当配置中心有修改时（由本地定时任务/job实现），后端服务会收到一个事件通知，
 * 把这些配置同步到使用了该配置项的bean中，而不只是把最新配置信息同步到本地，
 * 本地和ioc容器是不一样的，更新到本地（如本地缓存、数据库或文件等）一般是通过定时任务来实现的，
 * 而刷新到ioc容器则是通过spring-event机制来实现的，前者只是保存，后者则是直接在项目中使用起来了！！！
 * 而我们的目的也是真正在项目中使用起来，此时就需要在Bootstrap.yml中配置refresh=true，
 * 同时对于通过@Value注解获取配置文件中的数据的情况，还需要在对应的bean上加上@RefreshScope注解！
 * 对于nacos，它默认就是true！！！
 * 逻辑：就是发布一个RefreshEvent事件，
 * 之后就会有对应的监听者去更新对应bean的属性！
 *
 * @author zoutongkun
 * @date 2022/9/30 12:49
 */
public class ConfigContextRefresher implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    @Resource
    private ConfigCenterConfig configCenterConfig;
    /**
     * 初始化该bean时就已经启动了一个定时任务去监听配置中心的文件是否更新了，
     * 当更新时就会添加一个该类的回调，具体就是执行registerListeners方法，
     * 该方法会发布一个RefreshEvent事件，由带有@refreshScope注解的监听器进行监听，
     * 具体就是SpringCloud中RefreshEventListener类会去监听这个事件，
     * 一旦监听到这个事件，它就会刷新注入到对象的属性！！！
     */
    @Resource
    private ConfigService configService;

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //ioc容器初始化完成后，就会发布一个ApplicationReadyEvent事件，当前类就会监听该事件，
        //监听到后，会给每一个从远程配置中心获取到的配置文件都注册一个配置文件更新的监听器/回调方法，以便于动态刷新ioc容器！
        //它属于项目的初始化准备工作！真正触发是在实例化ConfigService对象时，
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
     * 这里我们就通过一个配置文件来做示范了。
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
