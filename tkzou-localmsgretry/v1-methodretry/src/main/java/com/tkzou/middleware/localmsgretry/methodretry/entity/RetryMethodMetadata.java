package com.tkzou.middleware.localmsgretry.methodretry.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:唯一标识一个方法信息的dto，用于执行失败后将该方法的信息发送到mq以重试
 * <p>
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryMethodMetadata {
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private String parameterTypes;
    /**
     * 参数值
     */
    private String args;
}
