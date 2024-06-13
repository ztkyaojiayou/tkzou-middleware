package com.tkzou.middleware.xxljobautoregister.support;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * job登录服务
 *
 * @author zoutongkun
 */
@Service
public class JobLoginService {
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY_TIME = 3;

    /**
     * xxl-job服务地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;
    /**
     * 用户名
     */
    @Value("${xxl.job.admin.username}")
    private String username;
    /**
     * 密码，其实就是accessToken
     */
    @Value("${xxl.job.admin.password}")
    private String password;

    private final Map<String, String> loginCookie = new HashMap<>();

    /**
     * 登录
     */
    public void login() {
        String url = adminAddresses + "/login";
        HttpResponse response = HttpRequest.post(url)
                .form("userName", username)
                .form("password", password)
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> cookie.getName().equals("XXL_JOB_LOGIN_IDENTITY")).findFirst();
        if (!cookieOpt.isPresent()) {
            throw new RuntimeException("get xxl-job cookie error!");
        }

        String value = cookieOpt.get().getValue();
        loginCookie.put("XXL_JOB_LOGIN_IDENTITY", value);
    }

    /**
     * 获取登录后的cookie
     *
     * @return
     */
    public String getCookie() {
        //重试3次，防止获取失败
        for (int i = 0; i < MAX_RETRY_TIME; i++) {
            //先获取，若没有就再登录一下
            String cookieStr = loginCookie.get("XXL_JOB_LOGIN_IDENTITY");
            if (cookieStr != null) {
                return "XXL_JOB_LOGIN_IDENTITY=" + cookieStr;
            }
            //登录，获取cookie
            login();
        }
        throw new RuntimeException("get xxl-job cookie error!");
    }

}
