package com.tkzou.middleware.mybatis.core.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * <p> 封装解析之后的sql和对应的参数字段 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/23 17:40
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BoundSql {
    /**
     * 解析之后的sql
     * 也即带？的sql
     * 原始sql在MappedStatement的sql中
     */
    private String sql;
    /**
     * 原始sql中的参数字段，如“id”，“name”等
     * todo 要求使用者在使用时按sql中的顺序编写，这里也是按此顺序进行保存！！！
     */
    private List<String> parameterMappings;

}
