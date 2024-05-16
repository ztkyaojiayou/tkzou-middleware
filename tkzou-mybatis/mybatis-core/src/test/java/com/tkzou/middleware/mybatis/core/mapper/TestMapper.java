package com.tkzou.middleware.mybatis.core.mapper;

import com.tkzou.middleware.mybatis.core.annotations.Param;
import com.tkzou.middleware.mybatis.core.annotations.Select;
import com.tkzou.middleware.mybatis.core.entity.User;

/**
 * <p> 测试Mapper </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:06
 */
public interface TestMapper {

    @Select("select * from t_user where id = #{id}")
    User selectOne(@Param("id") Integer id);

}
