package com.tkzou.middleware.transaction.localmsgretry.config;

import com.tkzou.middleware.transaction.localmsgretry.mapper.LocalMsgRetryRecordDao;
import com.tkzou.middleware.transaction.localmsgretry.mapper.LocalMsgRetryRecordMapper;
import com.tkzou.middleware.transaction.localmsgretry.service.MethodRetryService;
import com.tkzou.middleware.transaction.localmsgretry.support.MethodRetryConfigurer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Description:springboot自动配置类
 * 通过spi机制统一导出bean
 * Date: 2024-08-06
 *
 * @author zoutongkun
 */
@Configuration
@EnableScheduling
@MapperScan(basePackageClasses = LocalMsgRetryRecordMapper.class)
@ComponentScan("com.tkzou.middleware.localmsgretry.methodretry")
public class MethodRetryAutoConfiguration {

    @Nullable
    protected Executor executor;

    /**
     * 收集并设置自定义的线程池
     * Collect any {@link AsyncConfigurer} beans through autowiring.
     */
    @Autowired
    void setConfigurers(ObjectProvider<MethodRetryConfigurer> configurers) {
        Supplier<MethodRetryConfigurer> configurer = SingletonSupplier.of(() -> {
            List<MethodRetryConfigurer> candidates =
                configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
            }
            return candidates.get(0);
        });
        executor =
            Optional.ofNullable(configurer.get()).map(MethodRetryConfigurer::getExecutor).orElse(ForkJoinPool.commonPool());
    }

    @Bean
    public MethodRetryService buildMethodRetryService(LocalMsgRetryRecordDao dao) {
        return new MethodRetryService(dao, executor);
    }
}
