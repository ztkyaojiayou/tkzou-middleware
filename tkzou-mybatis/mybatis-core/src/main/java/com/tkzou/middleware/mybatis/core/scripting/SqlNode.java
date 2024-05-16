package com.tkzou.middleware.mybatis.core.scripting;

/**
 * <p> SQL节点 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/29 02:41
 */
public interface SqlNode {

    void apply(DynamicContext context);

}
