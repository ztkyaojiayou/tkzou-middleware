package com.tkzou.middleware.spring.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import com.tkzou.middleware.spring.beans.BeansException;
import com.tkzou.middleware.spring.beans.PropertyValue;
import com.tkzou.middleware.spring.beans.factory.config.AutowireCapableBeanFactory;
import com.tkzou.middleware.spring.beans.factory.config.BeanDefinition;
import com.tkzou.middleware.spring.beans.factory.config.BeanPostProcessor;
import com.tkzou.middleware.spring.beans.factory.config.BeanReference;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象工厂类
 * 这是个抽象类，父类的getBeanDefinition方法交由子类实现
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 15:02
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    /**
     * 默认使用无参构造函数实例化对象
     * 经测试，这里使用新策略CglibSubclassingInstantiationStrategy也是ok的！！！
     */
    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();

    /**
     * 根据beanName和对应的BeanDefinition（也即class对象）创建bean对象
     * 1.根据class对象，利用反射，生成bean对象
     * 2.将该对象与beanName绑定，并存入bean容器中！！！
     * 3.同时，返回该bean对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        return doCreateBean(beanName, beanDefinition);
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        //1.获取class对象
//        Class beanClass = beanDefinition.getBeanClass();
        Object bean;
        try {
            //2.根据反射生成bean对象（其实这还不叫bean，只是一个刚被实例化的对象而已！！！）
            //走完所有的生命周期才叫bean！！！
            //todo 易知这里只适⽤于bean有⽆参构造函数的情况
//            bean = beanClass.newInstance();
            //更新：因为新增了专门的接口，有两个实现类，这里做兼容
            bean = createBeanInstance(beanDefinition);
            //2.1再为该bean填充属性（多个，且可能是对象实例）
            //todo 若在这里直接注入属性，则对于有属性的类是正常注入的，
            //     但是对于没有属性的对象则会报NPE错，比如之前的HelloSpringService类，
            //     这里先注掉，后续会兼容！！！
            applyPropertyValues(beanName, bean, beanDefinition);
            //新增：执行bean的初始化和和BeanPostProcessor的前置和后置处理方法（核心）
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("bean:" + beanName + "实例化失败", e);
        }

        //3.将该beanName和生成的bean对象绑定，并存入bean容器中！！！
        addSingleton(beanName, bean);

        //4.同时返回该生成的bean对象
        return bean;
    }

    /**
     * bean的初始化（核心）
     * 不是抽象方法
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        Object wrappedBean = this.applyBeanPostProcessorBeforeInitialization(bean, beanName);
        //执行bean的初始化的方法
        // todo 后续再实现
        this.invokeInitMethods(beanName, wrappedBean, beanDefinition);
        //执行BeanPostProcessor的后置处理
        wrappedBean = this.applyBeanPostProcessorAfterInitialization(bean, beanName);
        return wrappedBean;
    }

    /**
     * 执行bean的初始化的方法
     *
     * @param beanName
     * @param wrappedBean
     * @param beanDefinition
     */
    protected void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {
        //TODO 后面再实现
        System.out.println("执行bean[" + beanName + "]的初始化方法");
    }

    /**
     * 为bean填充属性
     * 使用反射+set方法设置
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            //循环设置各属性的值
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
                //1.获取属性
                //1.1属性名称
                String name = propertyValue.getName();
                //1.2属性值
                Object value = propertyValue.getValue();
                //1.2.1新增校验，判断该属性值是否也为bean
                if (value instanceof BeanReference) {
                    //若是，则表示当前bean依赖该bean，则先实例化该bean
                    //todo 注意：由于不想增加代码的复杂度和理解难度，暂时不⽀持循环依赖，后续再议！
                    BeanReference beanReference = (BeanReference) value;
                    //先获取该bean（若没有，则会先实例化该bean）
                    value = super.getBean(beanReference.getBeanName());
                }
                //2.再通过反射设置属性
                //todo 注意：而不是通过所谓的全参构造器来构造，因为该方法本质也是实例化
                //  但我们的思路并不是通过全参构造器直接实例化该bean，
                //  而是先使用无参构造器实例化，再使用set的方式设置属性，这一点务必注意！！！
                //原方法
//                setFieldValue(beanDefinition, bean, name, value);
                //改用现成工具类完成
                BeanUtil.setFieldValue(bean, name, value);

            }
        } catch (Exception e) {
            throw new BeansException("属性注入错误，该bean为： " + beanName, e);
        }
    }

    /**
     * 生成bean实例
     * 默认使用无参构造器生成
     *
     * @param beanDefinition
     * @return
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition) {
        return instantiate(beanDefinition);
    }

    /**
     * 生成bean实例
     * 该方法其实也就是一种简单的封装
     *
     * @param beanDefinition
     * @return
     * @throws BeansException
     */
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        return getInstantiationStrategy().instantiate(beanDefinition);
    }

    /**
     * 获取bean实现的策略
     * <p>
     * 注意：没有什么高大上，就是根据高内聚的原则对类内的成员变量做的简单的封装，
     * 且可以通过idea的快捷键Alt+insert的Delegate Methods生成！！！
     * 其实就是get方法！！！
     *
     * @return
     */
    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    /**
     * 通过反射设置属性
     *
     * @param bean
     * @param name
     * @param value
     */
    protected void setFieldValue(BeanDefinition beanDefinition, Object bean, String name, Object value) {
        Class beanClass = beanDefinition.getBeanClass();
        try {
            //1.根据属性的set方法设置属性
            //1.1根据name获得类中属性对象，此时返回的是该属性的全类名！！！
            //如：private com.tkzou.middleware.spring.beans.factory.config.BeanDefinition.beanClass
            Field field = beanClass.getDeclaredField(name);
            //1.2再获取对应的类型，如BeanDefinition
            Class<?> fieldType = field.getType();

            //2.根据方法名获取对应的set方法
            //2.1构造set方法名
            String setMethodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            //2.2获取对应的set方法--根据方法名称和入参类型（这样才能确定一个具体的方法呀！！！因为可能有重载！）
            Method setMethod = beanClass.getDeclaredMethod(setMethodName, fieldType);

            //3.调用该set方法，设置值--给这个bean设置值
            setMethod.invoke(bean, value);
        } catch (Exception e) {
            throw new BeansException("Error setting property values for property name: " + name, e);
        }
    }

    /**
     * set方法
     *
     * @param instantiationStrategy
     */
    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        //易知，从这里就开始获取所有的BeanPostProcessor，
        //也即此时我们自定义的实现类都会被扫描到并依次执行里面的这个postProcessBeforeInitialization方法！！！
        //这个思路很重要，这也是我们自定义扩展点能生效的原因！！！
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            //执行初始化前的方法
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            //判空一下，为空时返回原始bean对象
            if (ObjectUtils.isEmpty(current)) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) {
        //同理
        Object result = existingBean;
        //同上，从这里就开始获取所有的BeanPostProcessor，
        //也即此时我们自定义的实现类都会被扫描到并依次执行里面的这个postProcessAfterInitialization方法！！！
        //这个思路很重要，这也是我们自定义扩展点能生效的原因！！！
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            //执行初始化后的方法
            Object current = processor.postProcessAfterInitialization(result, beanName);
            //判空一下，为空时返回原始bean对象
            if (ObjectUtils.isEmpty(current)) {
                return result;
            }
            result = current;
        }
        return result;
    }
}
