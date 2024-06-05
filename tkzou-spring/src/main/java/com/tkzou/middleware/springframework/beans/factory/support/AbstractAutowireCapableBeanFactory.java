package com.tkzou.middleware.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.PropertyValue;
import com.tkzou.middleware.springframework.beans.PropertyValues;
import com.tkzou.middleware.springframework.beans.factory.BeanFactoryAware;
import com.tkzou.middleware.springframework.beans.factory.DisposableBean;
import com.tkzou.middleware.springframework.beans.factory.InitializingBean;
import com.tkzou.middleware.springframework.beans.factory.config.*;
import com.tkzou.middleware.springframework.core.convert.ConversionService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
     * 初始化方法名
     */
    public static final String AFTER_PROPERTIES_SET = "afterPropertiesSet";
    /**
     * 默认使用无参构造函数实例化对象
     * 经测试，这里使用新策略CglibSubclassingInstantiationStrategy也是ok的！！！
     */
    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();
    /**
     * 是否允许循环依赖
     * 默认是true，也即是允许的，
     * 也即当出现了循环依赖时spring自己需要解决！
     */
    private boolean allowCircularReferences = true;

    /**
     * 创建bean
     * 也即根据beanName和对应的BeanDefinition（也即class对象）创建bean对象
     * 1.先执行实例化前的方法，走用户自定义的实例化逻辑，
     * 1.1若用户自定义了，则直接使用该bean，不再走spring自己的生命周期了（这种情况很少）
     * 1.2否则，就走spring的生命周期来创建bean，
     * 具体就是根据class对象，利用反射，生成bean对象
     * 2.将该beanName和bean绑定，并存入bean容器中！
     * 3.同时，返回该bean对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        //1.执行实例化前的逻辑，即使用用户自定义的创建bean的逻辑直接创建bean！
        Object bean = resolveBeforeInstantiation(beanName, beanDefinition);
        //若有，则直接返回了，不再走spring自己的生命周期了，很少用。
        if (ObjectUtil.isNotEmpty(bean)) {
            return bean;
        }
        //2.否则才走正常的bean生命周期，对于单例bean，会将其加入到ioc容器中（包括代理对象）！
        return doCreateBean(beanName, beanDefinition);
    }

    /**
     * 执行InstantiationAwareBeanPostProcessor的方法，
     * 一般是执行用户自定义的后置处理器，
     * 比如可以直接为当前bean创建一个bean，而不走spring自己的生命周期啦！
     * 很少用，只是spring也把这个扩展点暴露出来了。
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        //执行InstantiationAwareBeanPostProcessor的postProcessBeforeInstantiation方法
        //用于直接生成一个bean
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean != null) {
            //因为创建完了bean对象，也就相当于初始化完成了，
            //于是执行一下所有后置处理器中的该方法，也即执行bean初始化之后的逻辑
            //核心逻辑是根据项目中配置的切面来判断是否需要为该bean生成代理对象！
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    /**
     * 执行InstantiationAwareBeanPostProcessor的方法，
     * 比如直接由使用者返回一个对象，此时就以这个对象为准了！
     *
     * @param beanClass
     * @param beanName
     * @return
     */
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class beanClass, String beanName) {
        //遍历出所有的InstantiationAwareBeanPostProcessor，
        //这里一般是执行用户自定义的后置处理器，比如可以直接为当前bean创建一个bean！
        //todo 可以算是适配器模式！
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                //执行该接口的方法，直接创建一个对象
                //只要有一个处理器创建了一个对象，那么其他的就不再执行了！
                Object result =
                        ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation
                                (beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * 创建bean的核心逻辑
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
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

            //为解决循环依赖问题，将实例化后的bean放进缓存中提前暴露
            //解决该问题的关键在于何时将实例化后的bean放进容器中，设置属性前还是设置属性后。现
            //有的执⾏流程，bean实例化后并且设置属性后会被放进singletonObjects单例缓存中。如果
            //我们调整⼀下顺序，当bean实例化后就放进singletonObjects单例缓存中，提前暴露引⽤，
            //然后再设置属性，就能解决上⾯的循环依赖问题！！！
            //但这样还只能解决非代理的bean的循环依赖问题，原因是放进⼆级缓存
            //earlySingletonObjects中的bean是实例化后的bean，⽽放进⼀级缓存singletonObjects中
            //的bean是代理对象（代理对象在BeanPostProcessor#postProcessAfterInitialization中
            //返回），两个缓存中的bean不⼀致。⽐如上⾯的例⼦，如果A被代理，那么B拿到的a是实例
            //化后的A，⽽a是被代理后的对象，即b.getA() != a。
            //更新：解决代理对象的循环依赖问题，使用三级缓存！

            //判断是否是单例，是否产生了循环依赖，是否需要解决循环依赖
            boolean earlySingletonExposure = (beanDefinition.isSingleton() && this.allowCircularReferences &&
                    isSingletonCurrentlyInCreation(beanName));
            //为true时提前暴露/创建三级缓存
            if (earlySingletonExposure) {
                Object finalBean = bean;
                //提前暴露/保存三级缓存，即把创建对象的工厂先保存起来--核心！！！
                //具体是在getSingleton()方法中调用这个lambda表达式来创建对象！！！
                //这里并不创建对象！！！
                //另外，getEarlyBeanReference方法中的这三个参数其实是具体的值，就是从当前上下文取的！
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
                //原始写法
//                addSingletonFactory(beanName, new ObjectFactory<Object>() {
//                    //这是实现了ObjectFactory接口的一个实现类的一个具体对象，
//                    // 这个方法就是这个实现类中的具体实现，
//                    //可以看到，这个方法本身没有参数，那怎么又可以使用下面这三个参数呢？
//                    //答：这三个并不是参数，而是这个实现类中定义的三个字段，且已经被赋值了，也即值已经写死了！
//                    //值从哪里来？就是从当前这个上下文中取的！！！
//                    //至于为什么可以这么取就先不深究了！
//                    @Override
//                    public Object getObject() throws BeansException {
//                        //这三个参数其实是具体的值，就是从当前上下文取的！
//                        return getEarlyBeanReference(beanName, beanDefinition, finalBean);
//                    }
//                });
            }

            //实例化bean之后执行，返回false，则直接返回bean了，不再往下执行。
            //todo 当前这里没有具体的逻辑，可以跳过
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }

            //todo 在spring 5.x中，与属性填充相关的逻辑被抽成了一个叫populateBean的方法，后续可以跟上。
            //3.在设置bean属性之前，允许BeanPostProcessor修改属性值
            applyBeanPostprocessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);

            //2.1再为该bean填充上述属性（多个，且可能是对象实例）
            //todo 若在这里直接注入属性，则对于有属性的类是正常注入的，
            //     但是对于没有属性的对象则会报NPE错，比如之前的HelloSpringService类，
            //     这里先注掉，后续会兼容！！！
            applyPropertyValues(beanName, bean, beanDefinition);
            //新增：执行bean的初始化和和BeanPostProcessor的前置和后置处理方法（核心）
            //但这里可能会创建代理对象！！！
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("bean:" + beanName + "实例化失败", e);
        }

        //5.判断当前bean是否有销毁方法，若将其注册/保存一下
        //就是判断当前bean实现DisposableBean接口
        this.registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        //6.解决代理bean的循环依赖问题，使用三级缓存！
        //这个bean已经既有可能是原始bean，也有可能是代理bean啦！！！
        Object exposedObject = bean;
        //区分是否为原型bean，只有单例bean才走三级缓存逻辑，才注册到ioc容器！
        //todo 那原型的代理bean怎么办？
        //  在initializeBean方法中创建了！！！
        if (beanDefinition.isSingleton()) {
            //7.从三大缓存中重新获取bean，
            //此时返回的要么是原bean，要么就是一个代理对象！
            exposedObject = getSingleton(beanName);
            //3.再将该beanName和最终生成的bean对象绑定，并存入bean容器中！
            this.addSingleton(beanName, exposedObject);
        }
        //4.同时返回该生成的bean对象
        return exposedObject;
    }

    /**
     * 获取提前暴露的bean的核心逻辑
     * 就是执行所有的InstantiationAwareBeanPostProcessor后置处理器
     * 中的getEarlyBeanReference方法来获取bean
     * （可能是代理对象，也可能就是原对象，与切面有关，但这不重要！）
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     * @return
     */
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        //扫描所有的后置处理器
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            //只执行所有的InstantiationAwareBeanPostProcessor后置处理器的getEarlyBeanReference方法
            //一般也就一个，这里就是DefaultAdvisorAutoProxyCreator
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                //执行它的getEarlyBeanReference方法来最终获取需要提前暴露的bean
                exposedObject = ((InstantiationAwareBeanPostProcessor) bp).getEarlyBeanReference(exposedObject,
                        beanName);
            }
        }
        return exposedObject;
    }

    /**
     * bean实例化后执行，如果返回false，不执行后续设置属性的逻辑
     * 当前这里没有具体的逻辑
     *
     * @param beanName
     * @param bean
     * @return
     */
    private boolean applyBeanPostProcessorsAfterInstantiation(String beanName, Object bean) {
        boolean continueWithPropertyPopulation = true;
        //也是扫描所有的后置处理器
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            //找出所有的InstantiationAwareBeanPostProcessor后置处理器
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                //在实例化bean之后执行所有的InstantiationAwareBeanPostProcessor中的postProcessAfterInstantiation方法
                //如果返回false，就不再执行后续的设置属性的逻辑
                if (!((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessAfterInstantiation(bean,
                        beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
        return continueWithPropertyPopulation;
    }

    /**
     * 在设置bean属性之前，允许BeanPostProcessor修改属性值
     * 这里主要就是解析@Value和@Autowired注解的属性值，
     * 并将其添加到beanDefinition中
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyBeanPostprocessorsBeforeApplyingPropertyValues(String beanName, Object bean,
                                                                       BeanDefinition beanDefinition) {
        //同样是遍历所有的后置处理器，找出所有的InstantiationAwareBeanPostProcessor后置处理器
        //如AutowiredAnnotationBeanPostProcessor，用于处理@Value和@Autowired注解
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                //先解析出具体的属性值
                PropertyValues pvs =
                        ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (pvs != null) {
                    //再添加到beanDefinition中（这里还没有直接注入到bean中！）
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }

    /**
     * 注册有销毁方法的bean，也即继承自DisposableBean的bean或有自定义的销毁方法
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        //需要区分bean类型，只有单例bean才需要执行销毁方法!
        if (beanDefinition.isSingleton()) {
            //需要实现DisposableBean接口
            if (bean instanceof DisposableBean || StringUtils.isNotEmpty(beanDefinition.getDestroyMethodName())) {
                //包装一下,变成DisposableBeanAdapter，即把我们自定义的实现了DisposableBean接口的bean统一都包装成DisposableBeanAdapter
                this.registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
            }
        }
    }

    /**
     * bean的初始化（核心）
     * 不是抽象方法
     * 要注意的是：bean的初始化不是实例化，实例化先于初始化！！！
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        //1.处理实现了aware接口的bean，将当前类对象赋值到目标类中！
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }

        //1.执行BeanPostProcessor的前置处理方法
        Object wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        //2.执行bean的初始化的方法（核心）
        this.invokeInitMethods(beanName, wrappedBean, beanDefinition);
        //3.执行BeanPostProcessor的后置处理方法
        //这里也会创建代理对象！！！
        wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    /**
     * 执行bean的初始化的方法
     *
     * @param beanName
     * @param wrappedBean
     * @param beanDefinition
     */
    protected void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) throws Exception {
        //TODO 后面再实现
        if (wrappedBean instanceof InitializingBean) {
            ((InitializingBean) wrappedBean).afterPropertiesSet();
        }
        String initMethodName = beanDefinition.getInitMethodName();
        //去当前bean的类中查找该afterPropertiesSet/初始化方法
        if (StrUtil.isNotEmpty(initMethodName) && !(wrappedBean instanceof InitializingBean && AFTER_PROPERTIES_SET.equals(initMethodName))) {
            //使用反射获取方法对象
            Method initMethod = ClassUtil.getPublicMethod(beanDefinition.getBeanClass(), initMethodName);
            if (initMethod == null) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with " +
                        "name '" + beanName + "'");
            }
            initMethod.invoke(wrappedBean);
        }
    }

    /**
     * 为bean填充属性（包括在xml中手动配置的和使用@Autowired或@Value注解的字段
     * 使用反射+set方法设置
     * 这里就会注入在配置文件中配置的类型转换器，也即就把类型转换器初始化啦！！！
     *
     * @param beanName
     * @param bean
     * @param beanDefinition 此时
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            //循环设置各属性的值，包括类型转换器！
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
                //1.获取属性
                //1.1属性名称
                String name = propertyValue.getName();
                //1.2属性值，分为引用类型和非引用类型，其中后者需要考虑是否需要进行类型转换！
                //理解为我们设想的是全局类型转换器
                Object value = propertyValue.getValue();
                //1.2.1判断该属性值是否也为bean，类型转换器和对象类型的属性就会在这个分支中注入！
                if (value instanceof BeanReference) {
                    //若是，则表示当前bean依赖该bean，则先实例化该bean
                    //todo 注意：由于不想增加代码的复杂度和理解难度，暂时不⽀持循环依赖，后续再议！
                    BeanReference beanReference = (BeanReference) value;
                    //先获取该bean（若没有，则会先单独创建该bean）
                    //此时就可能产生循环依赖！
                    value = super.getBean(beanReference.getBeanName());
                } else {
                    //1.2.2否则，对于非引用类型，再判断是否需要进行类型转换
                    Class<?> sourceType = value.getClass();
                    Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), name);
                    ConversionService conversionService = getConversionService();
                    if (conversionService != null) {
                        //类型转换
                        if (conversionService.canConvert(sourceType, targetType)) {
                            value = conversionService.convert(value, targetType);
                        }
                    }
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
     * todo 目前已经改为使用hutool中的工具类来实现了
     *
     * @param bean
     * @param name
     * @param value
     */
    protected void setFieldValue(BeanDefinition beanDefinition, Object bean, String name, Object value) {
        Class beanClass = beanDefinition.getBeanClass();
        try {
            //1.根据属性的set方法设置属性
            //1.1根据name获得类中属性对象，此时返回的是该属性的全类名！
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
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        //易知，从这里就开始获取所有的BeanPostProcessor，
        //也即此时我们自定义的实现类都会被扫描到并依次执行里面的这个postProcessBeforeInitialization方法！！！
        //这个思路很重要，这也是我们自定义扩展点能生效的原因！！！
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            //执行初始化前的方法
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            //判空一下，为空时返回原始bean对象
            if (ObjectUtils.isEmpty(current)) {
                continue;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        //最终返回的bean
        Object result = existingBean;
        //同上，从这里就开始获取所有的BeanPostProcessor，
        //也即此时我们自定义的实现类都会被扫描到并依次执行里面的这个postProcessAfterInitialization方法！！！
        //这个思路很重要，这也是我们自定义扩展点能生效的原因！！！
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            //执行初始化后的方法，这里就包含了生成代理对象的处理器！
            Object current = processor.postProcessAfterInitialization(result, beanName);
            //判空一下，为空时返回原始bean对象
            if (ObjectUtils.isEmpty(current)) {
                continue;
            }
            result = current;
        }
        return result;
    }
}
