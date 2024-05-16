package com.tkzou.middleware.mybatis.core.plugin;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * <p> 拦截器-责任链 </p>
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/26 02:33
 */
public class InterceptorChain {
    /**
     * 保存多个拦截器
     */
    List<Interceptor> interceptorList = Lists.newArrayList();

    /**
     * 为指定对象执行所有拦截器！
     * 比如SimpleExecutor
     *
     * @param target 需要添加拦截器逻辑的对象，如SimpleExecutor
     * @return
     */
    public Object pluginAll(Object target) {
        //循环执行所有拦截器逻辑
        for (Interceptor interceptor : this.interceptorList) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    /**
     * 获取所有拦截器
     *
     * @return
     */
    public List<Interceptor> getInterceptorList() {
        return Collections.unmodifiableList(this.interceptorList);
    }

}
