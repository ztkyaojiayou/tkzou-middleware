package com.tkzou.middleware.mybatis.core.plugin;

import com.tkzou.middleware.mybatis.core.executor.statement.PreparedStatementHandler;
import com.tkzou.middleware.mybatis.core.executor.statement.StatementHandler;
import com.tkzou.middleware.mybatis.core.mapping.BoundSql;

import java.sql.Connection;

/**
 * <p> 分页插件 </p>
 * 分页拦截器
 * 作用或流程：
 * 就是mybatis会先给StatementHandler接口的子类生成一个代理对象，如PreparedStatementHandler
 * 其在执行prepare方法时就会走实际的增强逻辑，
 * 也即在目标方法prepare执行前先执行当前分页插件中的intercept方法
 * 参考mybatis-plus中的MybatisPlusInterceptor创建，它接管了mybatis的插件机制，同时在该插件中*
 * 又集成了自己的插件逻辑，即拦截器InnerInterceptor，比如分页插件、乐观锁插件、多租户插件等，非常妙！！！
 * 参考：https://blog.csdn.net/weixin_67727883/article/details/138337309
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 01:26
 */
@Intercepts({
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class})
})
public class LimitInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) {
//        System.out.println("分页插件start");
        //强转一下，其实不太合理，使用泛型最佳！
        PreparedStatementHandler psh = (PreparedStatementHandler) invocation.getTarget();
        //获取转为？后的sql
        BoundSql boundSql = psh.getBoundSql();
        String sql = boundSql.getSql();
        //防止执行多次query操作时重复添加limit
        if (sql.contains("select") && !sql.contains("LIMIT")) {
            boundSql.setSql(sql + " LIMIT 2");
        }
        //执行原方法
        Object result = invocation.proceed();
//        System.out.println("分页插件end");
        return result;
    }

    /**
     * 获取指定对象的代理对象
     * 该代理对象的增强逻辑中会执行当前拦截器的intercept逻辑！
     * 比如代理Executor接口，如SimpleExecutor
     *
     * @param target
     * @param <T>
     * @return
     */
    @Override
    public <T> T plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
