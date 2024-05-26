package com.tkzou.middleware.springframework.core.convert.converter;

import java.util.Set;

/**
 * 实际的类型转换接口
 * 也即对实际需求的落地
 * @author zoutongkun
 */
public interface GenericConverter {
    /**
     * 获取源类型和目标类型的class
     *
     * @return
     */
    Set<ConvertiblePair> getConvertibleTypes();

    /**
     * 类型转换，核心方法，就是通过它来实际干活的！
     * 即把目标对象从源类型转为目标类型
     * 易知需要一个对应的类型转换器
     * 这就可以集成前面定义的类型转换器啦！
     *
     * @param source     目标对象
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 转换后的对象
     */
    Object convert(Object source, Class sourceType, Class targetType);

    /**
     * 封装一下源类型和目标类型的class
     */
    final class ConvertiblePair {
        /**
         * 源类型class
         */
        private final Class<?> sourceType;
        /**
         * 目标类型class
         */
        private final Class<?> targetType;

        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return this.sourceType;
        }

        public Class<?> getTargetType() {
            return this.targetType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != ConvertiblePair.class) {
                return false;
            }
            ConvertiblePair other = (ConvertiblePair) obj;
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);

        }

        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }
    }
}
