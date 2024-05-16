package com.tkzou.middleware.mybatis.core.session;

/**
 * <p> 生产SqlSession </p>
 * 这些类其实都是不断重构、集成和封装的！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 01:57
 */
public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

}
