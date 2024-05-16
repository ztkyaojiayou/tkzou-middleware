package com.tkzou.middleware.mybatis.core.scripting;

import lombok.SneakyThrows;

/**
 * <p> 静态文本sql </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/29 02:41
 */
public class StaticTextSqlNode implements SqlNode {

    private String text; // eg: select * from t_user

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @SneakyThrows
    @Override
    public void apply(DynamicContext context) {
        context.appendSql(this.text);
    }

}
