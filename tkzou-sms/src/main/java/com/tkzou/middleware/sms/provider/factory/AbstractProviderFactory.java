package com.tkzou.middleware.sms.provider.factory;

import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 短信服务提供商抽象类
 * 这里使用了泛型来定义，且使用了extends，也可以使用supper
 * 且定义了两个泛型
 * 但无法使用通配符？，因为它是一种实参类型，而非形参类型！！！
 * 另外，在泛型方法的定义中无法使用通配符？和上下界！
 *
 * @author zoutongkun
 */
public abstract class AbstractProviderFactory<S extends SmsClient, C extends SmsProviderConfig> implements SmsProviderFactory<S, C> {
    /**
     * 当前短信服务提供商的配置类
     * 在springboot中解析配置文件时就会使用到！
     * 目的就是根据配置文件来选择使用何种服务提供商！
     */
    private Class<C> configClass;

    /**
     * 构造方法，在这里获取当前短信服务提供商的配置类
     * 子类会复用该方法！
     */
    public AbstractProviderFactory() {
        //获取一下当前类的泛型参数类型，这里就是获取到配置类的class对象
        //务必掌握！
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            //获取到当前类的所有泛型参数，是个数组！
            Type[] typeArguments = paramType.getActualTypeArguments();
            if (typeArguments.length > 1 && typeArguments[1] instanceof Class) {
                //按泛型的定义顺序获取即可！
                configClass = (Class<C>) typeArguments[1];
            }
        }
    }

    /**
     * 获取当前短信服务提供商的配置类
     * 定义在当前抽象类中即可，子类直接使用！
     *
     * @return 配置类
     */
    @Override
    public Class<C> getConfigClass() {
        return configClass;
    }

    public static void test2(AbstractProviderFactory<? extends SmsClient, ? extends SmsProviderConfig> t) {

    }

    public static void test3(AbstractProviderFactory<SmsClient, ? extends SmsProviderConfig> t) {

    }

}
