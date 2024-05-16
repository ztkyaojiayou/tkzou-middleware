package com.tkzou.middleware.mybatis.core.scripting;

import com.tkzou.middleware.mybatis.core.parsing.BindingTokenHandler;
import com.tkzou.middleware.mybatis.core.parsing.GenericTokenParser;
import lombok.SneakyThrows;

/**
 * <p> 文本sql </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/29 02:41
 */
public class TextSqlNode implements SqlNode {

    private String text; // eg: select * from t_user where id = ${id}

    public TextSqlNode(String text) {
        this.text = text;
    }

    @SneakyThrows
    @Override
    public void apply(DynamicContext context) {
        BindingTokenHandler tokenHandler = new BindingTokenHandler(context);
        GenericTokenParser genericTokenParser = new GenericTokenParser("${", "}", tokenHandler);
        String sql = genericTokenParser.parse(this.text);
        context.appendSql(sql);
    }

}
