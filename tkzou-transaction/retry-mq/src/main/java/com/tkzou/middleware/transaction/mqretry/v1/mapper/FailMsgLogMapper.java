package com.tkzou.middleware.transaction.mqretry.v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tkzou.middleware.transaction.mqretry.v1.entity.FailMsgLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息处理失败记录表 Mapper 接口
 *
 * @author zoutongkun
 */
@Mapper
public interface FailMsgLogMapper extends BaseMapper<FailMsgLog> {

}
