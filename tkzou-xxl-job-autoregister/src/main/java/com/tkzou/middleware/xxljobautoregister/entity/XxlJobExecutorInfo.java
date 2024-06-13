package com.tkzou.middleware.xxljobautoregister.entity;

import lombok.Data;

/**
 * job执行器信息
 * copy from xxl-job
 *
 * @author zoutongkun
 */
@Data
public class XxlJobExecutorInfo {
    /**
     * 执行器id
     */
    private int id;
    /**
     * 执行器名称
     */
    private String appName;
    /**
     * 执行器描述
     */
    private String title;
    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    private int addressType;
    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     */
    private String addressList;
}
