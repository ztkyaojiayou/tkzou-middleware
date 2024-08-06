package com.tkzou.middleware.xxljobautoregister.core;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tkzou.middleware.xxljobautoregister.annotation.XxlAutoRegister;
import com.tkzou.middleware.xxljobautoregister.entity.XxlJobExecutorInfo;
import com.tkzou.middleware.xxljobautoregister.entity.XxlJobHandlerInfo;
import com.tkzou.middleware.xxljobautoregister.support.JobExecutorService;
import com.tkzou.middleware.xxljobautoregister.support.JobHandlerService;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自动注册xxl-job任务的处理器
 * 使用事件监听机制异步注册，核心类
 *
 * @author zoutongkun
 */
@Component
public class XxlJobAutoRegisterProcessor implements ApplicationListener<ApplicationReadyEvent>,
        ApplicationContextAware {

    private static final Log log = LogFactory.get();

    private ApplicationContext applicationContext;

    @Autowired
    private JobExecutorService jobExecutorService;

    @Autowired
    private JobHandlerService jobHandlerService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 监听ApplicationReadyEvent事件
     * 容器初始化完成之后就开始注册项目中所有的xxl-job任务
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        //注册执行器
        addJobExecutor();
        //注册job任务
        addJobHandler();
    }

    /**
     * 注册执行器
     */
    private void addJobExecutor() {
        //若注册过了就跳过
        if (jobExecutorService.isRegistered()) {
            return;
        }
        //注册，本质就是向xxl-job服务器发送对应的http请求
        if (jobExecutorService.registerExecutor()) {
            log.info("auto register xxl-job group success!");
        }
    }

    /**
     * 注册job任务
     */
    private void addJobHandler() {
        List<XxlJobExecutorInfo> allJobExecutor = jobExecutorService.getAllJobExecutor();
        XxlJobExecutorInfo xxlJobExecutorInfo = allJobExecutor.get(0);
        //获取所有beanName
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        //遍历所有beanName，找出带@XxlJob注解的方法的bean
        for (String beanDefinitionName : beanDefinitionNames) {
            //当前bean
            Object bean = applicationContext.getBean(beanDefinitionName);
            //找出该bean中所有带@XxlJob注解的方法
            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            //遍历job方法，一个一个注册
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method curExecuteMethod = methodXxlJobEntry.getKey();
                XxlJob curXxlJob = methodXxlJobEntry.getValue();
                //自动注册
                //只注册带了@XxlJobAutoRegister注解的方法
                if (curExecuteMethod.isAnnotationPresent(XxlAutoRegister.class)) {
                    XxlAutoRegister xxlAutoRegister = curExecuteMethod.getAnnotation(XxlAutoRegister.class);
                    //当前job名称
                    String curHandlerName = curXxlJob.value();
                    List<XxlJobHandlerInfo> allJobHandler = jobHandlerService.getAllJobHandler(xxlJobExecutorInfo.getId(), curHandlerName);
                    if (!allJobHandler.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobHandlerInfo> first = allJobHandler.stream()
                                .filter(xxlJobHandlerInfo -> xxlJobHandlerInfo.getExecutorHandler().equals(curXxlJob.value()))
                                .findFirst();
                        //若已存在，即表示已注册，则跳过
                        if (first.isPresent()) {
                            continue;
                        }
                    }
                    // 自动注册到xxl-job 暂定Handle名称规则beanName#MethodName
                    registerToXxlJob(xxlJobExecutorInfo, curExecuteMethod, curXxlJob, xxlAutoRegister);
                }
            }
        }
    }

    /**
     * 注册到xxl-job
     *
     * @param xxlJobExecutorInfo
     * @param curExecuteMethod
     * @param curXxlJob
     * @param xxlAutoRegister
     */
    private void registerToXxlJob(XxlJobExecutorInfo xxlJobExecutorInfo, Method curExecuteMethod, XxlJob curXxlJob, XxlAutoRegister xxlAutoRegister) {
        //注册到本地
        String handlerName = curXxlJob.value();
        registerToLocal(handlerName, curExecuteMethod);
        //注册到远程xxl-job服务器
        registerToRemote(xxlJobExecutorInfo, curXxlJob, xxlAutoRegister);
    }

    /**
     * 注册任务到xxl-job服务端
     *
     * @param xxlJobExecutorInfo
     * @param curXxlJob
     * @param xxlAutoRegister
     */
    private void registerToRemote(XxlJobExecutorInfo xxlJobExecutorInfo, XxlJob curXxlJob, XxlAutoRegister xxlAutoRegister) {
        //创建job信息
        XxlJobHandlerInfo xxlJobHandlerInfo = createXxlJobHandlerInfo(xxlJobExecutorInfo, curXxlJob, xxlAutoRegister);
        //注册job
        jobHandlerService.addJobHandler(xxlJobHandlerInfo);
    }

    /**
     * 创建job
     *
     * @param xxlJobExecutorInfo
     * @param xxlJob
     * @param xxlAutoRegister
     * @return
     */
    private XxlJobHandlerInfo createXxlJobHandlerInfo(XxlJobExecutorInfo xxlJobExecutorInfo, XxlJob xxlJob, XxlAutoRegister xxlAutoRegister) {
        XxlJobHandlerInfo xxlJobHandlerInfo = new XxlJobHandlerInfo();
        //所处执行器id
        xxlJobHandlerInfo.setJobGroup(xxlJobExecutorInfo.getId());
        xxlJobHandlerInfo.setJobDesc(xxlAutoRegister.jobDesc());
        xxlJobHandlerInfo.setAuthor(xxlAutoRegister.author());
        //调度类型
        xxlJobHandlerInfo.setScheduleType("CRON");
        //cron表达式
        xxlJobHandlerInfo.setScheduleConf(xxlAutoRegister.cron());
        xxlJobHandlerInfo.setGlueType("BEAN");
        //所属执行器名称
        xxlJobHandlerInfo.setExecutorHandler(xxlJob.value());
        //job执行策略
        xxlJobHandlerInfo.setExecutorRouteStrategy(xxlAutoRegister.executorRouteStrategy());
        xxlJobHandlerInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobHandlerInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobHandlerInfo.setExecutorTimeout(0);
        xxlJobHandlerInfo.setExecutorFailRetryCount(0);
        xxlJobHandlerInfo.setGlueRemark("GLUE代码初始化");
        xxlJobHandlerInfo.setTriggerStatus(xxlAutoRegister.triggerStatus());

        return xxlJobHandlerInfo;
    }

    /**
     * 注册任务到本地
     *
     * @param handlerName   JobHandler名称
     * @param executeMethod 执行定时任务的方法
     */
    private void registerToLocal(String handlerName, Method executeMethod) {
        executeMethod.setAccessible(true);
        // xxl-job初始化和销毁方法对象，后续有需要再赋值
        Method initMethod = null;
        Method destroyMethod = null;
        //获取方法的Bean对象
        Object bean = applicationContext.getBean(executeMethod.getDeclaringClass());
        XxlJobExecutor.registJobHandler(handlerName, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));
    }
}
