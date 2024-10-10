package com.tkzou.middleware.transaction.localmsgretry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description: 本地消息表
 * 用于保存一个rpc方法的基本消息和对应的重试记录信息
 * <p>
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "method_retry_record", autoResultMap = true)
public class MethodRetryRecord {
    public final static byte STATUS_WAIT = 1;
    public final static byte STATUS_FAIL = 2;
    /**
     * 本地消息id，用于标识一个rpc方法的基本消息和对应的重试记录信息
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 请求快照参数json
     */
    @TableField(value = "retry_method_metadata_json", typeHandler = JacksonTypeHandler.class)
    private RetryMethodMetadata retryMethodMetadataJson;
    /**
     * 状态 1待执行 2已失败
     * 待执行表示第一次执行或重试了但未成功的情况
     * 执行成功就直接删除啦！！！
     */
    @TableField("status")
    @Builder.Default
    private byte status = MethodRetryRecord.STATUS_WAIT;
    /**
     * 下一次重试的时间
     */
    @TableField("next_retry_time")
    @Builder.Default
    private Date nextRetryTime = new Date();
    /**
     * 已经重试的次数
     * 默认0
     */
    @TableField("retry_times")
    @Builder.Default
    private Integer retryTimes = 0;
    /**
     * 最大重试次数
     */
    @TableField("max_retry_times")
    private Integer maxRetryTimes;
    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
