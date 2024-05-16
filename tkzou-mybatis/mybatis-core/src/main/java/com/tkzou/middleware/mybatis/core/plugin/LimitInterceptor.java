package com.tkzou.middleware.mybatis.core.plugin;

import com.tkzou.middleware.mybatis.core.executor.statement.PreparedStatementHandler;
import com.tkzou.middleware.mybatis.core.executor.statement.StatementHandler;
import com.tkzou.middleware.mybatis.core.mapping.BoundSql;

import java.sql.Connection;

/**
 * <p> 分页插件 </p>
 * 分页拦截器
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
     * 获取代理对象
     * 该代理对象的增强逻辑中会执行当前拦截器的逻辑！
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
