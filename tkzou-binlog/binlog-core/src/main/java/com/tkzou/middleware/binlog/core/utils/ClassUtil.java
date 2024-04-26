package com.tkzou.middleware.binlog.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class Utils
 *
 * @author zoutongkun
 */
public class ClassUtil {

    /**
     * 获取 Class 的 super 类 或 interface 类的首个泛型参数
     * <p>
     * 如果同时存在 super 与 interface 类, 优先级 super > interface
     */
    public static <T> Class<T> getGenericType(Class<?> cls) {
        Type superclass = cls.getGenericSuperclass();
        Type[] genericInterfaces = cls.getGenericInterfaces();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) superclass;
            Type[] argTypes = paramType.getActualTypeArguments();
            if (argTypes.length > 0) {
                return (Class<T>) argTypes[0];
            }
        } else if (genericInterfaces.length > 0) {
            Type type = genericInterfaces[0];
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                Type[] argTypes = paramType.getActualTypeArguments();
                if (argTypes.length > 0) {
                    return (Class<T>) argTypes[0];
                }
            }
        }
        return null;
    }

    public static Field getDeclaredField(Class clazz, String fieldName) {

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            if (field.getName().equals(toCamel(fieldName, "_")) || field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public static String toCamel(String str, String ch) {
        if (str.indexOf(ch) == -1)
            return str;
        String[] strings = str.split(ch);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0)
                stringBuffer.append(strings[i].toLowerCase());
            else
                stringBuffer.append(strings[i].substring(0, 1).toUpperCase()).append(strings[i].substring(1).toLowerCase());
        }
        return stringBuffer.toString();
    }
}
