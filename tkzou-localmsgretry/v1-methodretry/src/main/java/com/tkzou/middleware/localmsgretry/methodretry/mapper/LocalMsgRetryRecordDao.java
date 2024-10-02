package com.tkzou.middleware.localmsgretry.methodretry.mapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import com.tkzou.middleware.localmsgretry.methodretry.service.MethodRetryService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * <p>
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Component
public class LocalMsgRetryRecordDao extends ServiceImpl<LocalMsgRetryRecordMapper,
    MethodRetryRecord> {

    /**
     * 从数据库中捞出最近2min需要重试的方法记录
     * 注意：而没必要或不能无脑捞出全部的重试记录，因为可能会有很多（虽然没5s就扫一次的话，也不太可能会有很多啦）！！！
     * 另外，因为该表的记录可能互随着时间的推移越来越多，因此我们在执行成功后会直接删除该记录，
     * 而不是只维护一个“执行成功”的状态，保存没有意义呀！！
     *
     * @return
     */
    public List<MethodRetryRecord> getWaitRetryRecords() {
        Date now = new Date();
        //查2分钟前的失败数据。避免刚入库的数据被查出来
        DateTime afterTime = DateUtil.offsetMinute(now,
            (int) MethodRetryService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery()
            .eq(MethodRetryRecord::getStatus, MethodRetryRecord.STATUS_WAIT)
            .lt(MethodRetryRecord::getNextRetryTime, new Date())
            .lt(MethodRetryRecord::getCreateTime, afterTime)
            .list();
    }
}
