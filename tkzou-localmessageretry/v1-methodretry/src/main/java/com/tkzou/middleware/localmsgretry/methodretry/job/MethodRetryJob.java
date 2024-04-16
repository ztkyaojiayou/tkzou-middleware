package com.tkzou.middleware.localmsgretry.methodretry.job;

import com.tkzou.middleware.localmsgretry.methodretry.mapper.LocalMsgRetryRecordDao;
import com.tkzou.middleware.localmsgretry.methodretry.entity.MethodRetryRecord;
import com.tkzou.middleware.localmsgretry.methodretry.service.MethodRetryService;
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
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void methodRetry() {
        List<MethodRetryRecord> methodRetryRecords = localMsgRetryRecordDao.getWaitRetryRecords();
        for (MethodRetryRecord methodRetryRecord : methodRetryRecords) {
            methodRetryService.doAsyncInvoke(methodRetryRecord);
        }
    }

}
