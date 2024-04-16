package com.tkzou.middleware.localmsgretry.methodretry.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * 
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryMethodMetadata {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
