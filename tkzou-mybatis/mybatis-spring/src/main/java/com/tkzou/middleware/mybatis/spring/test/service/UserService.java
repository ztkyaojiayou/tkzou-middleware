package com.tkzou.middleware.mybatis.spring.test.service;


import com.tkzou.middleware.mybatis.spring.test.entity.User;

/**
 * <p> 用户service </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/5/5 04:03
 */
public interface UserService {

    User findOne(Integer id);

    void save(User user);

}
