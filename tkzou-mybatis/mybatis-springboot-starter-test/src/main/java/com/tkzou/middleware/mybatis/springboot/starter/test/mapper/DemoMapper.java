package com.tkzou.middleware.mybatis.springboot.starter.test.mapper;

import com.tkzou.middleware.mybatis.core.annotations.CacheNamespace;
import com.tkzou.middleware.mybatis.core.annotations.Param;
import com.tkzou.middleware.mybatis.core.annotations.Select;
import com.tkzou.middleware.mybatis.spring.test.entity.User;

/**
 * <p> demo Mapper </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:06
 */
@CacheNamespace
public interface DemoMapper {

    @Select("select * from t_user where id = #{id}")
    User findOne(@Param("id") Integer id);

}
