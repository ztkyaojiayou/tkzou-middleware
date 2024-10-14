package com.tkzou.middleware.mybatis.core.session;

import com.google.common.collect.Maps;
import com.tkzou.middleware.mybatis.core.datasource.PooledDataSourceFactory;
import com.tkzou.middleware.mybatis.core.executor.CacheExecutor;
import com.tkzou.middleware.mybatis.core.executor.Executor;
import com.tkzou.middleware.mybatis.core.executor.SimpleExecutor;
import com.tkzou.middleware.mybatis.core.executor.parameter.DefaultParameterHandler;
import com.tkzou.middleware.mybatis.core.executor.parameter.ParameterHandler;
import com.tkzou.middleware.mybatis.core.executor.resultset.DefaultResultSetHandler;
import com.tkzou.middleware.mybatis.core.executor.resultset.ResultSetHandler;
import com.tkzou.middleware.mybatis.core.executor.statement.PreparedStatementHandler;
import com.tkzou.middleware.mybatis.core.executor.statement.StatementHandler;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.plugin.InterceptorChain;
import com.tkzou.middleware.mybatis.core.plugin.LimitInterceptor;
import com.tkzou.middleware.mybatis.core.plugin.SqlLogInterceptor;
import com.tkzou.middleware.mybatis.core.transaction.JdbcTransaction;
import com.tkzou.middleware.mybatis.core.transaction.Transaction;
import com.tkzou.middleware.mybatis.core.type.IntegerTypeHandler;
import com.tkzou.middleware.mybatis.core.type.StringTypeHandler;
import com.tkzou.middleware.mybatis.core.type.TypeHandler;
import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> 核心配置 </p>
 * 配置项都往这里放！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 18:11
 */
@Data
public class Configuration {
    /**
     * 出入参类型处理器
     * 在创建该类时就会被初始化！
     */
    private Map<Class<?>, TypeHandler<?>> typeHandlerMap = Maps.newHashMap();
    /**
     * 用于保存所有mapper接口中所有方法的元信息，方便后续使用
     * 之后就可以直接get了，而无需每次都在执行sql时都单独解析一遍！
     * 没有区分单个mapper了，因为也没什么必要呀，反正id唯一！！！
     * key：mapper接口的全类名+具体方法名
     * eg: com.tkzou.middleware.mybatis.core.mapper.UserMapper.selectList --> mapper配置信息
     */
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();
    /**
     * 拦截器链
     */
    private InterceptorChain interceptorChain = new InterceptorChain();
    /**
     * 事务，包含了数据库连接的获取等
     */
    private Transaction transaction;
    /**
     * 是否使用Spring事务
     */
    private boolean isSpringTransaction = false;
    /**
     * 自定义数据源连接池
     * 实现DataSource即可即可
     */
    private DataSource dataSource = PooledDataSourceFactory.create();
    /**
     * 是否开启一级缓存
     */
    private boolean cacheEnabled = true;

    /**
     * 构造器
     * 可以有其他初始化逻辑，比如这里就同时把参数类型处理器和插件初始化了！
     */
    public Configuration() {
        //1.在new Configuration时，就把参数解析器同时初始化以备用，常用手法了¬！！！
        this.typeHandlerMap.put(Integer.class, new IntegerTypeHandler());
        this.typeHandlerMap.put(String.class, new StringTypeHandler());

        // 2.初始化并添加插件
        this.interceptorChain.addInterceptor(new LimitInterceptor());
        this.interceptorChain.addInterceptor(new SqlLogInterceptor());
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.isSpringTransaction = true;
    }

    /**
     * 获取事务对象
     *
     * @param autoCommit
     * @return
     */
    public Transaction getTransaction(boolean autoCommit) {
        //优先使用Spring事务
        if (this.isSpringTransaction) {
            return this.transaction;
        }
        //若没有，则使用Jdbc事务
        return new JdbcTransaction(this.dataSource, autoCommit);
    }

    /**
     * 添加一个mapper中某个方法的元信息
     *
     * @param ms
     */
    public void addMappedStatement(MappedStatement ms) {
        this.mappedStatements.put(ms.getId(), ms);
    }

    /**
     * 获取一个mapper中某个方法的元信息
     * 非常重要！
     *
     * @param id
     * @return
     */
    public MappedStatement getMappedStatement(String id) {
        return this.mappedStatements.get(id);
    }

    /**
     * 获取一个执行器
     * 默认是SimpleExecutor
     *
     * @param transaction
     * @return
     */
    public Executor newExecutor(Transaction transaction) {
        Executor executor = new SimpleExecutor(this, transaction);
        // 是否开启一级缓存，默认就开启
        if (this.cacheEnabled) {
            //使用装饰器封装SimpleExecutor的缓存执行器
            executor = new CacheExecutor(executor);
        }
        //添加拦截器逻辑，返回原对象或代理对象
        return (Executor) this.interceptorChain.pluginAll(executor);
    }

    /**
     * 获取结果处理器，也即DefaultResultSetHandler
     * 就是new出来的
     * 封装了拦截器的逻辑，因此会先走拦截器的逻辑！！！
     *
     * @return
     */
    public ResultSetHandler newResultSetHandler() {
        return (ResultSetHandler) this.interceptorChain.pluginAll(new DefaultResultSetHandler(this));
    }

    /**
     * 获取参数处理器，也即DefaultParameterHandler
     * 就是new出来的
     * 封装了拦截器的逻辑，因此会先走拦截器的逻辑！！！
     *
     * @return
     */
    public ParameterHandler newParameterHandler() {
        return (ParameterHandler) this.interceptorChain.pluginAll(new DefaultParameterHandler(this));
    }

    /**
     * 获取sql语句处理器，也即PreparedStatementHandler
     * 就是new出来的
     * 封装了拦截器的逻辑，因此会先走拦截器的逻辑！！！
     *
     * @param ms
     * @param parameter mapper中传入的查询参数，是个map，key为参数名称，value为对应的值
     * @return
     */
    public StatementHandler newStatementHandler(MappedStatement ms, Object parameter) {
        return (StatementHandler) this.interceptorChain.pluginAll(new PreparedStatementHandler(this, ms, parameter));
    }

}
