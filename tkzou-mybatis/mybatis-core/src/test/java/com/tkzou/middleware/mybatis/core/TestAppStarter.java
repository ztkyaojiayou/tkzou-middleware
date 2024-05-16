package com.tkzou.middleware.mybatis.core;

import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.mybatis.core.mapper.UserMapper;
import com.tkzou.middleware.mybatis.core.session.SqlSession;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactory;
import com.tkzou.middleware.mybatis.core.session.SqlSessionFactoryBuilder;
import org.junit.Test;

/**
 * <p> 测试 </p>
 * 核心测试主类
 * 这也是启动类！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/20 19:22
 */
public class TestAppStarter {

    @Test
    public void testAppStarter() throws Exception {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build();
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(JSONUtil.toJsonStr(userMapper.findOne(1)));
//        System.out.println(JSONUtil.toJsonStr(userMapper.selectList(1, "tkzou")));
//        System.out.println(JSONUtil.toJsonStr(userMapper.selectList(1, "tkzou")));

//        UserMapper userMapper2 = sqlSessionFactory.openSession().getMapper(UserMapper.class);
//        System.out.println(JSONUtil.toJsonStr(userMapper2.selectList(1, "tkzou")));
//        System.out.println(JSONUtil.toJsonStr(userMapper2.selectList(2, "xx")));

//        System.out.println(userMapper.selectOne(1));
//        System.out.println(userMapper.insert(User.builder().name(RandomUtil.randomString(5)).age(RandomUtil
//        .randomInt(1, 100)).build()));
//        System.out.println(userMapper.delete(5));
//        System.out.println(userMapper.update(2, "xxx"));
//        System.out.println(JSONUtil.toJsonStr(userMapper.selectList(1, "tkzou")));
        sqlSession.commit();
        sqlSession.close();
    }

}
