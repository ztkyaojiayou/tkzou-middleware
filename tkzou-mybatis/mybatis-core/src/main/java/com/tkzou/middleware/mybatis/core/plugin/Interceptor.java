package com.tkzou.middleware.mybatis.core.plugin;

/**
 * <p> 拦截器 </p>
 * 都属于增强逻辑！
 * 只是单独抽出来了一个接口，更易拓展，
 * 同时通过注册的方式，做到可插拔！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 01:25
 */
public interface Interceptor {
    /**
     * 拦截逻辑
     *
     * @param invocation 目标方法元信息/快照
     * @return 当前方法被拦截后的返回值
     */
    Object intercept(Invocation invocation);

    /**
     * 为目标类生成包含当前拦截器逻辑的代理对象
     * 代理增强类为Plugin
     * 且做到了方法级的拦截！！！
     * 务必掌握！
     *
     * @param target
     * @param <T>
     * @return
     */
    <T> T plugin(Object target);

}
