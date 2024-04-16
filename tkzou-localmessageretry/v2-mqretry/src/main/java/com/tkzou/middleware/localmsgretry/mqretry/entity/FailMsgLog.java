package com.tkzou.middleware.localmsgretry.mqretry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息处理失败记录表
 *
 * @author zoutongkun
 * @date 2023/10/03 00:41
 */
@TableName("mq_fail_msg_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailMsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号，作为重试的消息的唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属主题（而不是重试的主题！）
     */
    private String topic;

    /**
     * 消息key
     */
    @TableField("mq_key")
    private String mqKey;

    /**
     * 消息value
     */
    @TableField("mq_value")
    private String mqValue;

    /**
     * 已重发次数，默认为0，
     * 在重试job中维护，只要发送成功就加1
     */
    @TableField("resend_times")
    private Integer resendTimes;

    /**
     * 状态,1:未完成处理,2:已完成处理，默认为1
     * 在重试消费的消费者也即FailMsgConsumer中进行维护，也即只有真正消费成功才更新该状态
     */
    @TableField("mq_status")
    private Integer mqStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 最后操作时间
     */
    @TableField("op_time")
    private Date opTime;
}
