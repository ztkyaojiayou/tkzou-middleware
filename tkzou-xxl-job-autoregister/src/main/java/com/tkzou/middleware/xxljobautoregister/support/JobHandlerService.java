package com.tkzou.middleware.xxljobautoregister.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.xxljobautoregister.entity.XxlJobHandlerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * job任务/处理器服务
 * 也即具体的任务
 *
 * @author zoutongkun
 */
@Service
public class JobHandlerService {

    public static final int SUCCESS_CODE = 200;
    /**
     * xxl-job服务地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Autowired
    private JobLoginService jobLoginService;

    /**
     * 根据执行器id和任务id模糊查询所有job任务
     *
     * @param jobExecutorId
     * @param executorHandlerName
     * @return
     */
    public List<XxlJobHandlerInfo> getAllJobHandler(Integer jobExecutorId, String executorHandlerName) {
        String url = adminAddresses + "/jobinfo/pageList";
        HttpResponse response = HttpRequest.post(url)
                .form("jobGroup", jobExecutorId)
                .form("executorHandler", executorHandlerName)
                .form("triggerStatus", -1)
                .cookie(jobLoginService.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);

        return array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobHandlerInfo.class))
                .collect(Collectors.toList());
    }

    /**
     * 注册job到远程xxl-job服务器
     *
     * @param xxlJobHandlerInfo
     * @return
     */
    public Integer addJobHandler(XxlJobHandlerInfo xxlJobHandlerInfo) {
        String url = adminAddresses + "/jobinfo/add";
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobHandlerInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();

        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (code.equals(SUCCESS_CODE)) {
            return Convert.toInt(json.getByPath("content"));
        }
        throw new RuntimeException("add jobInfo error!");
    }

}
