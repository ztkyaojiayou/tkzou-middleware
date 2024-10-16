package com.tkzou.middleware.springframework.beans.factory.xml;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValue;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.springframework.beans.factory.config.BeanReference;
import com.tkzou.middleware.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.tkzou.middleware.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.tkzou.middleware.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.tkzou.middleware.springframework.core.io.Resource;
import com.tkzou.middleware.springframework.core.io.ResourceLoader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String SCOPE_ATTRIBUTE = "scope";
    /**
     * bean包扫描--核心
     * 比如：<context:base-package="com.tkzou.middleware.spring.service"/>
     */
    public static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    /**
     * 比如：<context:component-scan="com.tkzou.middleware.spring.service"/>
     */
    public static final String COMPONENT_SCAN_ELEMENT = "component-scan";
    /**
     * 懒加载
     */
    public static final String LAZYINIT_ATTRIBUTE = "lazyInit";

    /**
     * 两个构造器，都需要显式调用父类的构造器
     * 因为父类没有默认的构造器了！
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
        } catch (IOException | DocumentException e) {
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
     * todo 使用的是成熟的工具类dom4j来解析xml文件，但这不是重点，能解析就行！
     *
     * @param inputStream
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        //1.先解析xml中的bean
        //具体就是解析context:component-scan标签并扫描指定包中的类，提取类信息，组装成BeanDefinition
        loadFromXml(root);
        String scanPath = "";

        //2.再根据配置的包路径解析带bean注解的bean，如@Component等！！！
        // 即扫描包--核心方法，也即把带有@Component及其衍生注解的类信息组装成BeanDefinition对象！！！
        Element componentScan = root.element(COMPONENT_SCAN_ELEMENT);
        if (componentScan != null) {
            //如"com.tkzou.middleware.spring"
            scanPath = componentScan.attributeValue(BASE_PACKAGE_ATTRIBUTE);
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or" +
                    " null");
            }
        }
        loadFromScanPath(scanPath);
    }

    /**
     * 解析xml文件中的component-scan标签，
     * 并将该包路径下带有component注解的类生成BeanDefinition对象
     * 最重要，最核心，因为最常用呀！
     *
     * @param scanPath
     */
    private void loadFromScanPath(String scanPath) {
        scanPackage(scanPath);
    }

    /**
     * 解析xml文件中的bean标签，并生成BeanDefinition对象
     *
     * @param root
     * @throws DocumentException
     */
    private void loadFromXml(Element root) throws DocumentException {
        List<Element> beanList = root.elements(BEAN_ELEMENT);
        for (Element bean : beanList) {
            //1.解析bean标签
            String beanId = bean.attributeValue(ID_ATTRIBUTE);
            String beanName = bean.attributeValue(NAME_ATTRIBUTE);
            String className = bean.attributeValue(CLASS_ATTRIBUTE);
            String initMethodName = bean.attributeValue(INIT_METHOD_ATTRIBUTE);
            String destroyMethodName = bean.attributeValue(DESTROY_METHOD_ATTRIBUTE);
            String beanScope = bean.attributeValue(SCOPE_ATTRIBUTE);
            //是否懒加载
            String lazyInit = bean.attributeValue(LAZYINIT_ATTRIBUTE);

            Class<?> clazz;
            try {
                //1.1使用反射得到class对象
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeansException("Cannot find class [" + className + "]");
            }
            //beanName的定义策略
            //id优先于name
            beanName = StrUtil.isNotEmpty(beanId) ? beanId : beanName;
            if (StrUtil.isEmpty(beanName)) {
                //如果id和name都为空，将类名的第一个字母转为小写后作为bean的名称
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            //2.生成beanDefinition对象
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            //设置其他属性
            beanDefinition.setInitMethodName(initMethodName);
            beanDefinition.setDestroyMethodName(destroyMethodName);
            //是否懒加载
            beanDefinition.setLazyInit(BooleanUtil.toBoolean(lazyInit));
            //bean类型，就单例还是原型bean
            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            List<Element> propertyList = bean.elements(PROPERTY_ELEMENT);
            for (Element property : propertyList) {
                String propertyNameAttribute = property.attributeValue(NAME_ATTRIBUTE);
                String propertyValueAttribute = property.attributeValue(VALUE_ATTRIBUTE);
                String propertyRefAttribute = property.attributeValue(REF_ATTRIBUTE);

                if (StrUtil.isEmpty(propertyNameAttribute)) {
                    throw new BeansException("The name attribute cannot be null or empty");
                }

                Object value = propertyValueAttribute;
                if (StrUtil.isNotEmpty(propertyRefAttribute)) {
                    value = new BeanReference(propertyRefAttribute);
                }
                PropertyValue propertyValue = new PropertyValue(propertyNameAttribute, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }
            if (getRegistry().containsBeanDefinition(beanName)) {
                //beanName不能重名
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }
            //3.注册BeanDefinition，之后就可以通过getBean方法获取该bean了！
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 扫描注解Component的类，提取信息，组装成BeanDefinition
     *
     * @param scanPath
     */
    private void scanPackage(String scanPath) {
        //可能配置了多个包连路径
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(basePackages);
    }

}
