package com.tkzou.middleware.mybatis.core.proxy.original;

import org.junit.Test;

/**
 * <p> 测试代理模式 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/21 17:12
 */
public class TestProxy {

    @Test
    public void test() throws Exception {
        UserProxy userProxy = new UserProxy(new UserServiceImpl());
        Object xx = userProxy.selectList("xx");
        System.out.println(xx);
    }

}
