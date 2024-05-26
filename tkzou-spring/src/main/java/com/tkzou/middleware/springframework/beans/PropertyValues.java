package com.tkzou.middleware.springframework.beans;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bean的属性集合类
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 11:04
 */
public class PropertyValues {

    /**
     * 属性集合
     */
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    /**
     * 新增属性
     *
     * @param propertyValue
     */
    public void addPropertyValue(PropertyValue propertyValue) {
        propertyValueList.add(propertyValue);
    }

    /**
     * 获取属性
     * 是个数组
     *
     * @return
     */
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    /**
     * 根据propertyName匹配/获取对应的属性
     *
     * @param propertyName
     * @return
     */
    public PropertyValue getPropertyValue(String propertyName) {
        List<PropertyValue> resPropertyList =
                this.propertyValueList.stream().filter(o -> o.getName().equals(propertyName)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(resPropertyList)) {
            return null;
        }

        return resPropertyList.get(0);
    }

}
