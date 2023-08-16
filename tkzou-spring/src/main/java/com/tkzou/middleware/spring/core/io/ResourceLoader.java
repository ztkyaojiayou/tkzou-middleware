package com.tkzou.middleware.spring.core.io;

/**
 * 资源加载接口
 *
 * @author zoutongkun
 * @description: TODO
 * @date 2023/8/11 9:50
 */
public interface ResourceLoader {

    /**
     * 根据路径定位/查找资源
     *
     * @param location 资源/文件路径，默认实现中就为classpath
     * @return
     */
    Resource getResource(String location);
}
