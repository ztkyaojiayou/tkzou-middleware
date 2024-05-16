package com.tkzou.middleware.mybatis.core.proxy.jdk;

/**
 * <p> 用户service </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/21 17:08
 */
public interface UserService {

    Object selectList(String name);

    Object selectOne(String name);

}
