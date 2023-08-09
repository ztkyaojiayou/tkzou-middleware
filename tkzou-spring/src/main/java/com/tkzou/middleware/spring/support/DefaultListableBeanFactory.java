package com.tkzou.middleware.spring.support;

import com.tkzou.middleware.spring.BeansException;
import com.tkzou.middleware.spring.config.BeanDefinition;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

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
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
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
    protected BeanDefinition getBeanDefinition(String beanName) {
        //才容器中获取
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //判空
        if (ObjectUtils.isEmpty(beanDefinition)) {
            throw new BeansException("当前bean没有被定义：" + beanName);
        }
        //返回
        return beanDefinition;
    }

    /**
     * 维护/注册beanName和对应的BeanDefinition（关键方法）
     * 入参是beanName和对应的beanDefinition，刚好就可以通过在一个类上加一个注解（如大名鼎鼎的@Autowired）获取！！！
     *  todo  那么关键是在什么时候放进去的呢？这就是出口（入参），
     *    我们可以先在测试类中注册，测试，之后就可以通过扫描包下的注解来完成呀！！！
     *    比如，对应@Autowired注解，我们就可以根据该注解获取该类的beanName（首字母小写的类名）和对应的beanDefinition（该类的class对象）了呀！！！
     *   见测试类：BeanDefinitionAndBeanDefinitionRegistryTest
     * @param beanName
     * @param beanDefinition
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }
}
