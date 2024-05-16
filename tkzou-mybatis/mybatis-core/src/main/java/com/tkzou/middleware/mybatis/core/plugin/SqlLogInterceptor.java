package com.tkzou.middleware.mybatis.core.plugin;

import com.tkzou.middleware.mybatis.core.executor.statement.StatementHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * <p> sql日志插件 </p>
 * 日志打印拦截器
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 01:26
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "query",
                args = Statement.class),
        @Signature(
                type = StatementHandler.class,
                method = "update",
                args = Statement.class)
})
@Slf4j
public class SqlLogInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) {
//        System.out.println("sql插件start");
        //也是强转一下，最好是使用泛型
        PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
        //获取到sql，可以通过debug来确定如何替换无关的字符串！
        String sql = ps.toString().replace("com.mysql.cj.jdbc.ClientPreparedStatement: ", "");
        //打印一下sql
        System.err.println(sql);
        //
        Object result = invocation.proceed();
//        System.out.println("sql插件end");
        return result;
    }

    @Override
    public <T> T plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
