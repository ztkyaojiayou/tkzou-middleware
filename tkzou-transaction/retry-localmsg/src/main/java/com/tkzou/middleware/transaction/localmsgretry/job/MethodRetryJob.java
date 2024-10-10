package com.tkzou.middleware.transaction.localmsgretry.job;

import com.tkzou.middleware.transaction.localmsgretry.entity.MethodRetryRecord;
import com.tkzou.middleware.transaction.localmsgretry.mapper.LocalMsgRetryRecordDao;
import com.tkzou.middleware.transaction.localmsgretry.service.MethodRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 重试job，但在实际的项目中，一般会使用专门的定时任务框架实现，如xxl-job
 *
 * @author :zoutongkun
 * @date :2024/4/16 10:19 下午
 * @description :
 * @modyified By:
 */
@Component
public class MethodRetryJob {

    @Autowired
    private LocalMsgRetryRecordDao localMsgRetryRecordDao;
    @Autowired
    private MethodRetryService methodRetryService;

    /**
     * 重试
     * 每5s重试一次
     * 重试范围：最近2min的待执行状态的记录
     * 对于重试多次后还是失败的记录，可以再专门使用job处理！
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void methodRetry() {
        List<MethodRetryRecord> methodRetryRecords = localMsgRetryRecordDao.getWaitRetryRecords();
        for (MethodRetryRecord methodRetryRecord : methodRetryRecords) {
            //默认异步
            methodRetryService.doAsyncInvoke(methodRetryRecord);
        }
    }

}
