package com.tkzou.middleware.mybatis.springboot.starter.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试类
 */
@SpringBootTest
class MybatisSpringbootStarterTestApplicationTests {
    public static void main(String[] args) {
        Class<?> aClass = deduceMainApplicationClass("main");
        System.out.println(aClass);
    }

    @Test
    void contextLoads() {
        Class<?> aClass = deduceMainApplicationClass("contextLoads");
        System.out.println(aClass);
    }

    /**
     * 获取指定方法对应的类
     * 这也是springboot在启动获取main方法对应的类的方法
     *
     * @param methodName
     * @return
     */
    private static Class<?> deduceMainApplicationClass(String methodName) {
        try {
            //人为制造一个异常，再通过解析这个异常栈信息来获取！！！
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (methodName.equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

}
