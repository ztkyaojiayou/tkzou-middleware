package com.tkzou.middleware.spring.beans.factory;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.PropertyValues;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.tkzou.middleware.spring.core.io.DefaultResourceLoader;
import com.tkzou.middleware.spring.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 解析xml中的占位符，使用properties配置文件中的值替换
 * 如把xml中的${jdbc.url}替换成jdbc.properties文件中url的值
 * 实现起来很简单，理解即可。
 *
 * @author zoutongkun
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    public static final String PLACEHOLDER_PREFIX = "${";

    public static final String PLACEHOLDER_SUFFIX = "}";

    private String location;

    /**
     * 在所有的beanDefinition加载完成后，在bean实例化之前，
     * 用于修改beanDefinition的属性值！！！
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //1.先加载属性配置文件
        Properties properties = loadProperties();

        //2.再通过properties中的属性值替换xml中的占位符
        processProperties(beanFactory, properties);
    }

    /**
     * 加载属性配置文件
     *
     * @return
     */
    private Properties loadProperties() {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            return properties;
        } catch (IOException e) {
            throw new BeansException("Could not load properties", e);
        }
    }

    /**
     * 使用属性值替换占位符
     *
     * @param beanFactory
     * @param properties
     * @throws BeansException
     */
    private void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            resolvePropertyValues(beanDefinition, properties);
        }
    }

    /**
     * 替换的核心逻辑
     *
     * @param beanDefinition
     * @param properties
     */
    private void resolvePropertyValues(BeanDefinition beanDefinition, Properties properties) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof String) {
                //TODO 仅简单支持一个占位符的格式
                //该属性值来自xml配置文件中，此时有占位符，如${brand}，
                //需要从properties配置文件进行替换
                String strVal = (String) value;
                StringBuffer buf = new StringBuffer(strVal);
                int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
                int endIndex = strVal.indexOf(PLACEHOLDER_SUFFIX);
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    //取出占位符中的字段名称，如band
                    String propKey = strVal.substring(startIndex + 2, endIndex);
                    //从properties配置文件中获取对应的属性值
                    String propVal = properties.getProperty(propKey);
                    //再替换即可
                    buf.replace(startIndex, endIndex + 1, propVal);
                    propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), buf.toString()));
                }
            }
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
