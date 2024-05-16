package com.tkzou.middleware.mybatis.core.parsing;

/**
 * <p> 标记处理器 </p>
 * 这里使用了token这个说法，有一点点误导之嫌！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 01:41
 */
public interface TokenHandler {

    /**
     * 处理标记
     *
     * @param content 参数内容，如:如#{id}
     * @return 标记解析后的内容 eg: 参数名称 -> ?
     */
    String handleToken(String content);

}
