package com.tkzou.middleware.springboot.core.autoconfiguration;

/**
 * 自动配置bean接口
 * 模拟通过spi机制扫描bean
 * 任何导入的第三方依赖，只要将class对象放在resources的META-INF.services目录下，那么就都就可以被读取！
 *
 * @author zoutongkun
 */
public interface AutoConfiguration {
}
