package com.tkzou.middleware.springframework.beans.factory;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValue;
import com.tkzou.middleware.springframework.beans.PropertyValues;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.tkzou.middleware.springframework.core.io.DefaultResourceLoader;
import com.tkzou.middleware.springframework.core.io.Resource;
import com.tkzou.middleware.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取并解析配置文件！！！
 * 解析xml中的占位符，使用properties配置文件中的值替换
 * 如把xml中的${jdbc.url}替换成jdbc.properties文件中url的值
 * 实现起来很简单，理解即可。
 *
 * @author zoutongkun
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    public static final String PLACEHOLDER_PREFIX = "${";

    public static final String PLACEHOLDER_SUFFIX = "}";
    /**
     * 就是我们项目中的配置文件，比如applicant.properties或xml！！！
     */
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

        //3.最后注册占位符解析器，供解析@Value注解使用
        PlaceholderResolvingStringValueResolver valueResolver =
            new PlaceholderResolvingStringValueResolver(properties);
        beanFactory.addEmbeddedValueResolver(valueResolver);

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
                //解析占位符
                value = resolvePlaceholder((String) value, properties);
                //赋值
                propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), value));
            }
        }
    }

    /**
     * 解析单个占位符
     *
     * @param value      占位符，如${brand}
     * @param properties properties配置文件中的所有属性值
     * @return
     */
    private String resolvePlaceholder(String value, Properties properties) {
        //TODO 仅简单支持一个占位符的格式
        //该属性值来自xml配置文件中，此时有占位符，如${brand}，
        //需要从properties配置文件进行替换
        String strVal = value;
        StringBuffer buf = new StringBuffer(strVal);
        int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
        int endIndex = strVal.indexOf(PLACEHOLDER_SUFFIX);
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            //取出占位符中的字段名称，如band
            String propKey = strVal.substring(startIndex + 2, endIndex);
            String propVal = properties.getProperty(propKey);
            //再替换即可
            buf.replace(startIndex, endIndex + 1, propVal);
        }
        return buf.toString();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 内部类
     * 专门用于解析@Value注解中定义的属性值
     * 如：
     */
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
        /**
         * properties配置文件中的所有属性值
         */
        private Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        /**
         * 解析@Value注解中占位符的属性值
         *
         * @param strVal 如${brand}
         * @return
         */
        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
        }
    }
}
