package com.tkzou.middleware.mybatis.core;

import org.junit.Test;

public class TestMybatis {

    public static void main(String[] args) {
        // 打印字符串"test-mybatis!"到控制台
        System.out.println("test-mybatis!");
    }

    /**
     * 对于junit，目标方法必须是pubic，否则报错！
     */
    @Test
    public void test01() {
        System.out.println("test-mybatis！");
    }
}