package com.tkzou.middleware.spring.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.BeanReference;
import com.tkzou.middleware.spring.beans.factory.support.AbstractBeanDefinitionReader;
import com.tkzou.middleware.spring.beans.factory.support.BeanDefinitionRegistry;
import com.tkzou.middleware.spring.core.io.Resource;
import com.tkzou.middleware.spring.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

/**
 * 具体实现类--用于读取xml文件中的bean定义信息
 * 1.由于是具体实现类，那么必须实现抽象类中未实现的方法
 * 2.同时虽然该类没有定义新的成员变量，且虽然继承了抽象父类，
 * 3.但由于抽象父类中的成员变量为private，因此子类是无法继承的，
 * 但是，我们依旧可以使用其提供的get或set方法对其进行操作，
 * 此时就相当于是自己的成员变量，也即虽然没有直接拥有，但依旧有使用权！！！
 * 同样地，对于构造器，我们依旧可以使用父类的构造器将成员变量占为己有，
 * 具体而言，使用super就可以调用父类的构造器！！！
 * 4.由于从xml⽂件中读取的内容是String类型，所以属性仅⽀持String类型和引⽤其他Bean；后⾯会讲到类型转换器，以实现类型转换。
 *
 * @author :zoutongkun
 * @date :2023/8/23 8:46 下午
 * @description :
 * @modyified By:
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    /**
     * xml中的核心标签
     */
    public static final String BEAN_ELEMENT = "bean";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String REF_ATTRIBUTE = "ref";

    /**
     * 两个构造器，都调用父类现有的
     *
     * @param registry
     * @param resourceLoader
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 两个抽象类中为实现的方法，在具体子类中必须实现！
     */

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = this.getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        //再调用下面这个重载方法，很常见的手法，各种链式调用，习惯就好！
        this.loadBeanDefinitions(resource);
    }

    /**
     * 真正解析xml文件中的bean定义信息--关键
     *
     * @param resource
     * @throws BeansException
     */
    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try {
            InputStream inputStream = resource.getInputStream();
            try {
                //执行解析
                doLoadBeanDefinitions(inputStream);
            } finally {
                //切记要关闭流
                inputStream.close();
            }
        } catch (IOException e) {
            throw new BeansException("xml文件解析出错，路径为：" + resource, e);
        }
    }

    /**
     * 解析xml核心方法
     * 但解析过程不重要，关键是理解其核心逻辑
     * 1.解析xml文件中的bean定义信息，得到beanName和beanDefinition，生成BeanDefinition对象
     * 2.再注册BeanDefinition至beanDefinitionMap
     * 其中：获取beanName的逻辑为先id再name，最后再使用类名的第一个字母转为小写的名称
     * 另外，beanName在map中不能重复
     * todo 可以使用成熟的工具类来解析xml文件，比如dom4j，但这不是重点，先不管！
     *
     * @param inputStream
     */
    private void doLoadBeanDefinitions(InputStream inputStream) {
        Document document = XmlUtil.readXML(inputStream);
        Element root = document.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                if (BEAN_ELEMENT.equals(((Element) childNodes.item(i)).getNodeName())) {
                    //1.解析bean标签
                    Element bean = (Element) childNodes.item(i);
                    String id = bean.getAttribute(ID_ATTRIBUTE);
                    String name = bean.getAttribute(NAME_ATTRIBUTE);
                    String className = bean.getAttribute(CLASS_ATTRIBUTE);

                    Class<?> clazz = null;
                    try {
                        //1.1使用反射得到class对象
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new BeansException("Cannot find class [" + className + "]");
                    }
                    //beanName的定义策略
                    // id优先于name
                    String beanName = StrUtil.isNotEmpty(id) ? id : name;
                    if (StrUtil.isEmpty(beanName)) {
                        //如果id和name都为空，将类名的第一个字母转为小写后作为bean的名称
                        beanName = StrUtil.lowerFirst(clazz.getSimpleName());
                    }

                    //2.生成beanDefinition对象
                    BeanDefinition beanDefinition = new BeanDefinition(clazz);

                    for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                        if (bean.getChildNodes().item(j) instanceof Element) {
                            if (PROPERTY_ELEMENT.equals(((Element) bean.getChildNodes().item(j)).getNodeName())) {
                                //1.2解析property标签
                                Element property = (Element) bean.getChildNodes().item(j);
                                String nameAttribute = property.getAttribute(NAME_ATTRIBUTE);
                                String valueAttribute = property.getAttribute(VALUE_ATTRIBUTE);
                                String refAttribute = property.getAttribute(REF_ATTRIBUTE);

                                if (StrUtil.isEmpty(nameAttribute)) {
                                    throw new BeansException("The name attribute cannot be null or empty");
                                }

                                Object value = valueAttribute;
                                if (StrUtil.isNotEmpty(refAttribute)) {
                                    value = new BeanReference(refAttribute);
                                }
                                PropertyValue propertyValue = new PropertyValue(nameAttribute, value);
                                //设置属性
                                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                            }
                        }
                    }

                    //beanName不能重名
                    if (this.getRegistry().containsBeanDefinition(beanName)) {
                        throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
                    }
                    //3.注册BeanDefinition，之后就可以通过getBean方法获取该bean了！
                    this.getRegistry().registerBeanDefinition(beanName, beanDefinition);
                }
            }
        }

    }
}
