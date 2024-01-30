package com.tkzou.middleware.threadpool.dtp.v1.test;

import com.tkzou.middleware.threadpool.dtp.v1.config.DtpRefreshConfigV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zoutongkun
 */
@RestController
@RequestMapping("/threadpool")
public class DtpController {

    @Autowired
    private DtpRefreshConfigV1 dtpConfig;

    /**
     * 打印当前线程池的状态
     */
    @GetMapping("/print")
    public String printThreadPoolStatus() {
        return dtpConfig.printDtpStatus();
    }

    /**
     * 给线程池增加任务
     *
     * @param count
     */
    @GetMapping("/add")
    public String dynamicThreadPoolAddTask(int count) {
        dtpConfig.addTaskToDtp(count);
        return String.valueOf(count);
    }
}