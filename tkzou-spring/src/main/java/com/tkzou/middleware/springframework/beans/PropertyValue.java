package com.tkzou.middleware.springframework.beans;

/**
 * bean属性信息封装
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/10 11:01
 */
public class PropertyValue {
    /**
     * 属性名称
     */
    private final String name;

    /**
     * 属性值，可能是个对象！
     * todo 易知，关键问题是这个值从哪儿来？
     *      易知就可以联想到依赖注入的方式啦，比如@Value和@Autowired注解！
     */
    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
