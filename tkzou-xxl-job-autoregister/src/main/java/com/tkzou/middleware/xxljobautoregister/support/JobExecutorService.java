package com.tkzou.middleware.xxljobautoregister.support;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.xxljobautoregister.entity.XxlJobExecutorInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * job执行器服务
 * 在xxl-job中，执行器也叫Group
 * 一个执行器其实就是一个springboot服务！
 *
 * @author zoutongkun
 */
@Service
public class JobExecutorService {
    /**
     * xxl-job服务地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;
    /**
     * 当前项目配置的执行器名称
     */
    @Value("${xxl.job.executor.appname}")
    private String appName;
    /**
     * 当前项目配置的执行器描述信息
     */
    @Value("${xxl.job.executor.title}")
    private String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @Value("${xxl.job.executor.addressType:0}")
    private Integer addressType;

    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     */
    @Value("${xxl.job.executor.addressList:}")
    private String addressList;

    @Autowired
    private JobLoginService jobLoginService;

    /**
     * 根据当前项目配置的appname和title
     * 模糊查询获取所有job执行器
     *
     * @return
     */
    public List<XxlJobExecutorInfo> getAllJobExecutor() {
        String url = adminAddresses + "/jobgroup/pageList";
        HttpResponse response = HttpRequest.post(url)
                .form("appname", appName)
                .form("title", title)
                .cookie(jobLoginService.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);
        List<XxlJobExecutorInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobExecutorInfo.class))
                .collect(Collectors.toList());

        return list;
    }

    /**
     * 自动注册job执行器
     *
     * @return
     */
    public boolean registerExecutor() {
        String url = adminAddresses + "/jobgroup/save";
        HttpRequest httpRequest = HttpRequest.post(url)
                .form("appname", appName)
                .form("title", title);

        httpRequest.form("addressType", addressType);
        if (addressType.equals(1)) {
            if (Strings.isBlank(addressList)) {
                throw new RuntimeException("手动录入模式下,执行器地址列表不能为空");
            }
            httpRequest.form("addressList", addressList);
        }

        HttpResponse response = httpRequest.cookie(jobLoginService.getCookie())
                .execute();
        Object code = JSONUtil.parse(response.body()).getByPath("code");
        return code.equals(200);
    }

    /**
     * 检查当前项目/执行器是否已注册
     *
     * @return
     */
    public boolean isRegistered() {
        List<XxlJobExecutorInfo> allJobExecutor = getAllJobExecutor();
        Optional<XxlJobExecutorInfo> res = allJobExecutor.stream()
                //过滤出当前appname和title的执行器
                .filter(xxlJobExecutorInfo -> xxlJobExecutorInfo.getAppName().equals(appName)
                        && xxlJobExecutorInfo.getTitle().equals(title))
                .findAny();
        return res.isPresent();
    }

}
