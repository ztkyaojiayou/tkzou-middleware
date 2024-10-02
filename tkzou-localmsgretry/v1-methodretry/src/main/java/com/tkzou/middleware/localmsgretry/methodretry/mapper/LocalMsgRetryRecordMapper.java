package com.tkzou.middleware.localmsgretry.methodretry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author zoutongkun
 */
@Mapper
@Repository
public interface LocalMsgRetryRecordMapper extends BaseMapper<MethodRetryRecord> {

}
