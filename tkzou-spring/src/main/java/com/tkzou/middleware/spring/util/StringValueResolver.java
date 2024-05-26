package com.tkzou.middleware.spring.util;

/**
 * 字符串值解析器接口。
 * 比如，解析"${myProperty}"
 * 具体在PropertyPlaceholderConfigurer中的内部类PlaceholderResolvingStringValueResolver实现
 *
 * @author :zoutongkun
 * @date :2024/5/26 11:19 上午
 * @description :
 * @modyified By:
 */
public interface StringValueResolver {
    /**
     * 解析字符串值。
     * 比如，如果字符串值是"${myProperty}"，则应该返回系统属性"myProperty"的值。
     *
     * @param strVal
     * @return
     */
    String resolveStringValue(String strVal);
}
