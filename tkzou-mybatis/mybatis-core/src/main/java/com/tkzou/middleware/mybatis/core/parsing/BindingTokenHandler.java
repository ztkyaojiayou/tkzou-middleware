package com.tkzou.middleware.mybatis.core.parsing;

import com.tkzou.middleware.mybatis.core.scripting.DynamicContext;
import lombok.SneakyThrows;
import ognl.Ognl;

/**
 * <p> 参数处理器 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 01:44
 */
public class BindingTokenHandler implements TokenHandler {

    private DynamicContext context;

    public BindingTokenHandler(DynamicContext context) {
        this.context = context;
    }

    @SneakyThrows
    @Override
    public String handleToken(String content) {
        return String.valueOf(Ognl.getValue(content, this.context.getBindings()));
    }

}
