package com.tkzou.middleware.sms.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tkzou.middleware.sms.exception.SmsException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 使用huTool提供的httpClient
 * 当前项目的一大核心特点就是在发送短信时并不是直接引入官方的sdk，
 * 而是全部统一走http的方式，即根据各短信服务平台对发送请求的参数要求拼接请求url进行发送！
 * 这其实就是本质，各大官方sdk的底层也如此，只是做了更进一步的封装而已！
 * 使用了典型的单例模式！
 * 其实直接使用static的方式调用即可，大可不必如此复杂！
 *
 * @author zoutongkun
 */
@Component
public class HttpUtil {

    /**
     * 恶汉式
     */
    private static final HttpUtil INSTANCE = new HttpUtil();

    /**
     * 对外提供该单例对象
     *
     * @return
     */
    public static HttpUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 发送post json请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param body    请求体(json格式字符串)
     * @return 返回体
     */
    public JSONObject postJson(String url, Map<String, String> headers, String body) {
        try (HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(body)
                .execute()) {
            return JSONUtil.parseObj(response.body());
        } catch (Exception e) {
            throw new SmsException(e.getMessage());
        }
    }

    /**
     * 发送post json请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param body    请求体(map格式请求体)
     * @return 返回体
     */
    public JSONObject postJson(String url, Map<String, String> headers, Map<String, Object> body) {
        return postJson(url, headers, JSONUtil.toJsonStr(body));
    }

    /**
     * 发送post form 请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param body    请求体(map格式请求体)
     * @return 返回体
     */
    public JSONObject postFrom(String url, Map<String, String> headers, Map<String, Object> body) {
        try (HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .form(body)
                .execute()) {
            return JSONUtil.parseObj(response.body());
        } catch (Exception e) {
            throw new SmsException(e.getMessage());
        }
    }

    /**
     * 线程睡眠
     *
     * @param retryInterval 秒
     */
    public void safeSleep(int retryInterval) {
        ThreadUtil.safeSleep(retryInterval * 1000L);
    }
}
