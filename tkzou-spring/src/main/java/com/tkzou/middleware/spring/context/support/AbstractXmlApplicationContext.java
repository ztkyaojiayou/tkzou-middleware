package com.tkzou.middleware.spring.context.support;

import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.factory.support.DefaultListableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 继续来一个抽象类，完成抽象父类中的遗愿
 * 即加载BeanDefinition，最终解析xml文件，完成加载
 *
 * @author :zoutongkun
 * @date :2023/8/29 12:14 上午
 * @description :
 * @modyified By:
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
    /**
     * 加载BeanDefinition
     * 主要是通过解析xml文件中的定义来完成加载，
     * 逻辑很清晰，一步一步从抽象到具体实现，最终的载体是xml，也即配置文件
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    protected void loadBeanDefinition(DefaultListableBeanFactory beanFactory) throws BeansException {
        //1.创建xml资源加载器完成xml文件的解析
        //注意：这里易知需要传入一个ResourceLoader对象，
        // 但由于当前类就实现了该接口，因此直接传this即可！！！
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        //2.获取xml配置文件路径，交给子类实现
        String[] configLocations = getConfigLocations();
        //3.完成加载
        if (ObjectUtils.isNotEmpty(configLocations)) {
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * 获取xml配置文件路径
     *
     * @return
     */
    protected abstract String[] getConfigLocations();

}
