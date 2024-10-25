package com.tkzou.middleware.threadpool.dtp.v1.test;

import com.tkzou.middleware.threadpool.dtp.v1.core.core.EnableDtpExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 参考：https://mp.weixin.qq.com/s/PCl2rb0x5j053PHAf5wwPw
 *
 * @author zoutongkun
 * @Date 2023/5/20 16:53
 */
@SpringBootApplication
@EnableDtpExecutor
public class DtpExecutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DtpExecutorApplication.class, args);
    }
}
