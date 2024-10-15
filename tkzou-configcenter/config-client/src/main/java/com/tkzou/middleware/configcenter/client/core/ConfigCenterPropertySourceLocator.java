package com.tkzou.middleware.configcenter.client.core;

import com.tkzou.middleware.configcenter.client.config.ConfigCenterConfig;
import com.tkzou.middleware.configcenter.client.domain.ConfigFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 核心接口--步骤1
 * 负责从远程拉取配置，此时还是springboot服务首次加载
 * PropertySourceLocator就是起到在SpringCloud环境下从外部/远程如配置中心获取配置的作用（此时ioc容器正在初始化中，还并没有初始化完成！）。
 * PropertySourceLocator是一个接口，所以只要不同的配置中心实现这个接口，
 * 那么不同的配置中心就可以整合到了SpringCloud，从而实现从配置中心加载配置属性到Spring环境中了。
 * 参考：https://mp.weixin.qq.com/s/840rQ8GapAcQYZAcJ13sfQ
 * <p>
 *
 * @author zoutongkun
 * @date 2022/9/30 00:35
 */
@Slf4j
public class ConfigCenterPropertySourceLocator implements PropertySourceLocator {
    /**
     * 获取所有的配置文件加载器，用于解析我们的配置文件！！！
     * <p>
     * SpringBoot 的配置文件内置支持 properties、xml、yml、yaml 几种格式，
     * 其中 properties和xml 对应的Loader类为 PropertiesPropertySourceLoader，
     * 其中，yml和yaml对应的Loader类为 YamlPropertySourceLoader。
     * 观察这2个类可以发现，都实现自接口 PropertySourceLoader 。
     * 所以我们要新增支持别的格式的配置文件，就可以通过实现接口 PropertySourceLoader 来实现了。
     */
    private final List<PropertySourceLoader> propertySourceLoaderList =
        SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
            Thread.currentThread().getContextClassLoader());

    @Resource
    private ConfigService configService;

    @Resource
    private ConfigCenterConfig configCenterConfig;

    /**
     * 从配置中心加载最新配置
     * 项目启动的时候SpringCloud是如何从配置中心加载数据的，
     * 主要是通过新建一个容器，加载bootstrap配置文件和一些配置类，
     * 最后会调用PropertySourceLocator中的该方法来从配置中心获取到最新的配置信息。
     *
     * @param environment
     * @return
     */
    @Override
    public PropertySource<?> locate(Environment environment) {
        /**
         * 用于存储配置文件中的值，这个name即为配置文件的名称，比如实际项目中的主配置类就是properties，或其他自定义名称！
         */
        CompositePropertySource composite = new CompositePropertySource("tkzou-config");

        try {
            //解析并获取配置信息，此时还是springboot服务首次加载！！！
            loadConfig(composite, configCenterConfig.getConfigFileId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return composite;
    }

    /**
     * 根据所配置的文件的id，从配置中心拉取配置信息，然后解析配置。
     *
     * @param composite
     * @param fileId
     * @throws IOException
     */
    private void loadConfig(CompositePropertySource composite, String fileId) throws IOException {
        //1.从配置中心获取当前配置id的最新的配置信息
        ConfigFile newConfigFile = configService.getConfigByRpc(fileId);

        if (newConfigFile == null) {
            throw new RuntimeException(fileId + "未从远程配置中心找到对应的配置文件");
        }
        //2.遍历所有的配置文件解析器，匹配到可以解析当前配置文件的解析器进行解析
        //本质上属于适配器模式！
        for (PropertySourceLoader propertySourceLoader : propertySourceLoaderList) {
            //使用能解析当前配置文件的解析器解析配置文件
            //具体就是使用文件后缀匹配！
            if (Arrays.asList(propertySourceLoader.getFileExtensions()).contains(newConfigFile.getExtension())) {
                //3.解析配置文件中的属性值，返回的是个list，我们取第一个
                List<PropertySource<?>> configInfoList = propertySourceLoader.load(newConfigFile.getFileId(),
                    new ByteArrayResource(newConfigFile.getContent().getBytes(StandardCharsets.ISO_8859_1)));
                for (PropertySource<?> propertySource : configInfoList) {
                    //添加该属性至spring-cloud
                    composite.addFirstPropertySource(propertySource);
                }
            }
        }
    }

}
