package com.tkzou.middleware.mybatis.spring.test.mapper;

import com.tkzou.middleware.mybatis.core.annotations.CacheNamespace;
import com.tkzou.middleware.mybatis.core.annotations.Insert;
import com.tkzou.middleware.mybatis.core.annotations.Param;
import com.tkzou.middleware.mybatis.core.annotations.Select;
import com.tkzou.middleware.mybatis.spring.test.entity.User;

/**
 * <p> 用户Mapper </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:06
 */
@CacheNamespace
public interface UserMapper {

    @Select("select * from t_user where id = #{id}")
    User findOne(@Param("id") Integer id);

    @Insert("insert into t_user(name, age) values(#{user.name}, #{user.age})")
    Long insert(@Param("user") User user);

}
