package com.tkzou.middleware.sms.provider.impl.aliyun;

import com.tkzou.middleware.sms.common.constant.SmsSupplierConstant;
import com.tkzou.middleware.sms.provider.client.SmsClient;
import com.tkzou.middleware.sms.provider.config.SmsProviderConfig;
import com.tkzou.middleware.sms.provider.factory.AbstractProviderFactory;
import com.tkzou.middleware.sms.provider.impl.aliyun.config.AlibabaSmsConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * AlibabaSmsConfig
 * <p> 阿里云短信实现类工厂
 * 这些具体的厂商不在spring启动时一次性注入，而应该手动注册，且还应该有注销的方法，这样才是可插拔！
 *
 * @author :zoutongkun
 * 2024/4/8  14:54
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlibabaClientFactory extends AbstractProviderFactory<AlibabaSmsClient, AlibabaSmsConfig> {

    private static final AlibabaClientFactory INSTANCE = new AlibabaClientFactory();

    /**
     * 获取建造者实例
     *
     * @return 建造者实例
     */
    public static AlibabaClientFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 创建短信实现对象
     *
     * @param alibabaConfig 短信配置对象
     * @return 短信实现对象
     */
    @Override
    public AlibabaSmsClient createSmsClient(AlibabaSmsConfig alibabaConfig) {
        return new AlibabaSmsClient(alibabaConfig);
    }

    /**
     * 获取供应商
     *
     * @return 供应商
     */
    @Override
    public String getSupplier() {
        return SmsSupplierConstant.ALIBABA;
    }

    /**
     * 定义袋上下界的泛型方法
     *
     * @param s
     * @param v
     * @param <S>
     * @param <V>
     */
    public static <S extends SmsClient, V extends SmsProviderConfig> S test(S s, V v) {
        return s;
    }


    /**
     * 测试获取当前类的泛型参数类型
     * 务必掌握！
     *
     * @param args
     */
    public static void main(String[] args) {

        //使用泛型方法
        SmsClient test = test(new AlibabaSmsClient(new AlibabaSmsConfig()), new AlibabaSmsConfig());
        SmsClient test2 = test(new AlibabaSmsClient(new AlibabaSmsConfig()), new AlibabaSmsConfig());
        //获取一下配置类的class对象
        Type genericSuperclass = AlibabaClientFactory.class.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = paramType.getActualTypeArguments();
            if (typeArguments.length > 1 && typeArguments[1] instanceof Class) {
                Class<C> typeArgument = (Class<C>) typeArguments[1];
                System.out.println(typeArgument);
            }
        }
    }
}
