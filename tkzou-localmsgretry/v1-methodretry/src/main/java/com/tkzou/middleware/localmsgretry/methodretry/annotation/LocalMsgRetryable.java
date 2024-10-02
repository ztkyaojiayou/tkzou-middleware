package com.tkzou.middleware.localmsgretry.methodretry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本地消息重试注解
 * 保证rpc方法成功执行，比如发送redis、kafka或minio等
 * 即保证分布式事务中若第一个操作成功了，则后面的操作也一定同时成功，而不让它失败导致回滚第一个操作的情况！
 * 这就是我们常说的分布式事务的最终一致性保证的常用方案！
 * （至于消费端若消息执行失败，则我们可以自行使用补偿机制兜底或也使用一个本地消息表来配合job重试，
 * 这其实并不是分布式事务中一致性的解决范畴了，分布式事务主要也是解决要么同时成功要么同时失败，
 * 只是在分布式环境下还多了一个中间态，即超时，也即此时我们并不知道到底是成功还是失败，
 * 因此就需要我们自己来通过重试保证了！！！）
 * 如果在事务内的方法，会将操作记录入库，保证执行。
 * 要注意的是，当前注解没有处理方法的返回值，因此更适合不关心返回值的方法！！！
 *
 * @author zoutongkun
 */
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target(ElementType.METHOD)//作用在方法上
public @interface LocalMsgRetryable {

    /**
     * 默认3次
     *
     * @return 最大重试次数(包括第一次正常执行)
     */
    int maxRetryTimes() default 3;

    /**
     * 默认异步执行，先入库，后续异步执行，不影响主线程快速返回结果,
     * 毕竟失败了有重试，而且主线程的事务已经提交了，串行执行没啥意义。
     * 同步执行适合mq消费场景等对耗时不关心，但是希望链路追踪不被异步影响的场景。
     *
     * @return 是否异步执行
     */
    boolean isAsync() default true;
}
