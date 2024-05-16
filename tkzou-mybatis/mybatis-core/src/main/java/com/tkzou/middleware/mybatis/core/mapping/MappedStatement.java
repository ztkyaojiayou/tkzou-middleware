package com.tkzou.middleware.mybatis.core.mapping;

import com.tkzou.middleware.mybatis.core.cache.Cache;
import com.tkzou.middleware.mybatis.core.parsing.GenericTokenParser;
import com.tkzou.middleware.mybatis.core.parsing.ParameterMappingTokenHandler;
import com.tkzou.middleware.mybatis.core.scripting.DynamicContext;
import com.tkzou.middleware.mybatis.core.scripting.SqlNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p> 封装mapper中的每个方法的元信息 </p>
 * 用于保存mapper接口中每个方法的信息
 * 易知一个mapper接口会解析出多个MappedStatement
 * 后面会以map的形式统一封装到Configuration中
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 18:16
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MappedStatement {
    /**
     * mapper中一个方法的唯一标识 eg: com.zhengqing.demo.mapper.UserMapper.selectList
     * 定位到具体哪个mapper下的哪个方法
     */
    private String id;
    /**
     * 原始SQL eg: select * from t_user where id = #{id}
     * 此时还没有处理#{id}
     */
    private String sql;
    /**
     * 返回类型
     */
    private Class returnType;
    /**
     * SQL命令类型，如增删改查
     */
    private SqlCommandType sqlCommandType;
    /**
     * 是否查询多条数据
     * 即用于区分是selectOne还是selectList
     */
    private Boolean isSelectMany;
    /**
     * 二级缓存，而非一级缓存！
     */
    private Cache cache;
    /**
     * 动态SQL
     */
    private SqlNode sqlSource;

    public BoundSql getBoundSql(Object parameter) {
        if (this.sqlSource != null) {
            DynamicContext dynamicContext = new DynamicContext((Map<String, Object>) parameter);
            this.sqlSource.apply(dynamicContext);
            this.sql = dynamicContext.getSql();
        }

        // sql解析  #{}  --- ?
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String sql = genericTokenParser.parse(this.sql);
        List<String> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        return BoundSql.builder().sql(sql).parameterMappings(parameterMappings).build();
    }

    /**
     * 获取查询结果的缓存key
     * 源码中key的结构：id + offset + limit + sql + param value + environment id。
     * 参考：https://blog.csdn.net/qq_45483846/article/details/125983821
     *
     * @param parameter
     * @return
     */
    public String createCacheKey(Object parameter) {
        return this.id + ":" + this.sql + ":" + parameter;
    }

}
