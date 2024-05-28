package com.tkzou.middleware.springframework.beans.factory.support;

import com.tkzou.middleware.springframework.beans.BeansException;
import com.tkzou.middleware.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.tkzou.middleware.springframework.beans.factory.config.BeanDefinition;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

/**
 * beanDefinitionMap容器
 * 这是一个普通的类，也是ioc容器（也即bean容器）的入口
 * 用于管理beanName和对应的BeanDefinition，使用一个map维护/管理
 * 通过这两个参数就可以根据反射来得到对应的单例bean对象啦！！！
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/9 14:16
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    /**
     * 存放beanName和对应的BeanDefinition的map/容器
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 获取对应的beanDefinition对象
     * 就从这个beanDefinitionMap中获取
     * 易知，需要先将这个映射关系放进去！也即下面的registerBeanDefinition方法
     *
     * @param beanName
     * @return
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        //才容器中获取
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //判空
        if (ObjectUtils.isEmpty(beanDefinition)) {
            throw new BeansException("当前bean没有被定义：" + beanName);
        }
        //返回
        return beanDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        //需要区分是否为单例bean
        this.beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            //这里只创建单例bean，对于原型bean，则都是临时创建并使用的！
            if (beanDefinition.isSingleton()) {
                this.getBean(beanName);
            }
        });
    }

    /**
     * 维护/注册beanName和对应的BeanDefinition（关键方法）
     * 入参是beanName和对应的beanDefinition，刚好就可以通过在一个类上加一个注解（如大名鼎鼎的@Autowired）获取！！！
     *  todo  那么关键是在什么时候放进去的呢？这就是出口（入参），
     *    我们可以先在测试类中注册，测试，之后就可以通过扫描包下的注解来完成呀！！！
     *    比如，对应@Autowired注解，我们就可以根据该注解获取该类的beanName（首字母小写的类名）和对应的beanDefinition（该类的class对象）了呀！！！
     *   见测试类：BeanDefinitionAndBeanDefinitionRegistryTest
     *
     * @param beanName
     * @param beanDefinition
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> res = new HashMap<>();
        //遍历beanDefinitionMap
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            //isAssignableFrom:是用来判断子类和父类的关系的，或接口的实现类和接口的关系的,默认所有的类的终极父类都是Object
            //当A.isAssignableFrom(B)结果是true,则说明B可以转换成为A,也就是A可以由B转换而来
            if (type.isAssignableFrom(beanClass)) {
                //强转成泛型T，注意这里是(T)而不是<T>
                T bean = (T) this.getBean(beanName);
                res.put(beanName, bean);
            }
        });

        return res;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            //找出使用符合目标类型的beanName
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        //只取一个，若不止一个，则报错！
        if (beanNames.size() == 1) {
            //从ioc容器中取
            return getBean(beanNames.get(0), requiredType);
        }

        throw new BeansException(requiredType + "expected single bean but found " +
                beanNames.size() + ": " + beanNames);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        Set<String> beanNames = beanDefinitionMap.keySet();
        return beanNames.toArray(new String[0]);
    }
}
