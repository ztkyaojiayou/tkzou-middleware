package com.tkzou.middleware.springboot.core.condition;

import com.tkzou.middleware.springboot.core.annotation.ConditionalOnClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 按条件注入bean
 *
 * @author zoutongkun
 */
public class MyCondition implements Condition {

    public static final String VALUE = "value";

    /**
     * 条件匹配规则
     *
     * @param context
     * @param metadata
     * @return
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnClass.class.getName());
        String className = (String) annotationAttributes.get(VALUE);

        try {
            Class<?> clazz = context.getClassLoader().loadClass(className);
            return !ObjectUtils.isEmpty(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }

    }
}
