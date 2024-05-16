package com.tkzou.middleware.mybatis.core.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * <p> 代理对象执行方法时的元数据封装</p>
 * 即把代理对象中每个方法的信息封装和保存起来，这样就可以随时调用了！
 * 相当于就是每个方法的快照，其实在本地消息表的落地实现中也是相同的处理！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 01:31
 */
@Data
@AllArgsConstructor
public class Invocation {
    /**
     * 代理对象
     */
    private Object target;
    /**
     * 执行方法
     */
    private Method method;
    /**
     * 方法参数
     */
    private Object[] args;

    /**
     * 执行方法
     *
     * @return
     */
    @SneakyThrows
    public Object proceed() {
        return this.method.invoke(this.target, this.args);
    }

}
