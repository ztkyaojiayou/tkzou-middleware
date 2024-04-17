package com.tkzou.middleware.localmsgretry.mqretry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tkzou.middleware.localmsgretry.mqretry.entity.FailMsgLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息处理失败记录表 Mapper 接口
 * @author zoutongkun
 */
@Mapper
public interface FailMsgLogMapper extends BaseMapper<FailMsgLog> {

}
