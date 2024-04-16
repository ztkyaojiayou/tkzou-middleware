package com.tkzou.middleware.localmsgretry.methodretry.mapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import com.tkzou.middleware.localmsgretry.methodretry.mapper.LocalMsgRetryRecordMapper;
import com.tkzou.middleware.localmsgretry.methodretry.service.MethodRetryService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * 
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Component
public class LocalMsgRetryRecordDao extends ServiceImpl<LocalMsgRetryRecordMapper, MethodRetryRecord> {

    /**
     * 从数据库中捞出所有需要重试的方法记录
     *
     * @return
     */
    public List<MethodRetryRecord> getWaitRetryRecords() {
        Date now = new Date();
        //查2分钟前的失败数据。避免刚入库的数据被查出来
        DateTime afterTime = DateUtil.offsetMinute(now, (int) MethodRetryService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery()
                .eq(MethodRetryRecord::getStatus, MethodRetryRecord.STATUS_WAIT)
                .lt(MethodRetryRecord::getNextRetryTime, new Date())
                .lt(MethodRetryRecord::getCreateTime, afterTime)
                .list();
    }
}
