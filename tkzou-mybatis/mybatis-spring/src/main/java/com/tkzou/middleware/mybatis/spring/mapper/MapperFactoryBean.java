package com.tkzou.middleware.mybatis.spring.mapper;

import com.tkzou.middleware.mybatis.core.session.SqlSession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p> mapper工厂bean </p>
 *
 * @author zhengqingya
 * @description 用于自定义创建bean
 * @date 2024/5/5 05:04
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {
    /**
     * 需要被代理的mapper接口
     */
    private Class<T> mapperInterface;

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Autowired
    private SqlSession sqlSession;

    /**
     * 获取bean，即获取mapper接口的代理对象！！！
     *
     * @return
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        //获取代理对象
        return this.sqlSession.getMapper(this.mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }
}
