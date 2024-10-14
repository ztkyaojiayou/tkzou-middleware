package com.tkzou.middleware.mybatis.core.binding;

import com.google.common.collect.Maps;
import com.tkzou.middleware.mybatis.core.annotations.Param;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.mapping.SqlCommandType;
import com.tkzou.middleware.mybatis.core.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * <p> mapper代理类的增强逻辑 </p>
 * 其实是mapper接口的增强类，叫拦截器更合适！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/21 19:57
 */
public class MapperProxy implements InvocationHandler {
    /**
     * 操作jdbc的session
     */
    private SqlSession sqlSession;
    /**
     * 要代理的mapper接口的class
     * 没有其他作用，主要是为了拼接MappedStatement中的id
     */
    private Class mapperClass;

    public MapperProxy(SqlSession sqlSession, Class mapperClass) {
        this.sqlSession = sqlSession;
        this.mapperClass = mapperClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.获取mapper调用方法的参数名 -> 参数值
        //先保存一下
        Map<String, Object> paramValueMap = Maps.newHashMap();
        Parameter[] parameterList = method.getParameters();
        for (int i = 0; i < parameterList.length; i++) {
            Parameter parameter = parameterList[i];
            //获取方法中传入的参数，使用@Param注解来作映射！
            Param param = parameter.getAnnotation(Param.class);
            //即参数名，如user
            String paramName = param.value();
            //该参数名对应的参数值，
            //它可能是一个基础类型，如String或Integer,也可能是个对象，如user！
            Object arg = args[i];
            paramValueMap.put(paramName, arg);
        }
        //2.获取当前方法的所有原材料，也即MappedStatement
        String statementId = this.mapperClass.getName() + "." + method.getName();
        MappedStatement ms = this.sqlSession.getConfiguration().getMappedStatement(statementId);
        //3.获取crud操作类型
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        switch (sqlCommandType) {
            //插入
            case INSERT:
                return this.convertResult(ms, this.sqlSession.insert(statementId, paramValueMap));
            //删除
            case DELETE:
                return this.convertResult(ms, this.sqlSession.delete(statementId, paramValueMap));
            //更新
            case UPDATE:
                return this.convertResult(ms, this.sqlSession.update(statementId, paramValueMap));
            //查询
            case SELECT:
                //是否查询list
                if (ms.getIsSelectMany()) {
                    return this.sqlSession.selectList(statementId, paramValueMap);
                } else {
                    return this.sqlSession.selectOne(statementId, paramValueMap);
                }
            default:
                break;
        }
        return null;
    }

    /**
     * 处理结果类型，针对增删改操作，不针对查询操作！
     * 因为jdbc对于写操作都是返回int，即影响行数，
     * 但mapper接口中可能使用int、Integer、long、Long甚至是void来接收，
     * 因此我们需要适配一下这些case。
     *
     * @param ms
     * @param updateCount
     * @return
     */
    private Object convertResult(MappedStatement ms, int updateCount) {
        //mapper接口中该方法定义的返回值类型
        Class returnType = ms.getReturnType();
        //若为int、Integer型
        if (returnType == int.class || returnType == Integer.class) {
            return updateCount;
            //若为long、Long型
        } else if (returnType == long.class || returnType == Long.class) {
            return (long) updateCount;
            //若为void型
        } else if (returnType == void.class) {
            return null;
        }
        //其他类型不再适配，意义不大！
        throw new RuntimeException("该类sql的返回值不支持当前类型,类型为：" + returnType.getTypeName());
    }
}
