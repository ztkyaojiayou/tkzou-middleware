package com.tkzou.middleware.sms.core.callback;

import com.tkzou.middleware.sms.common.SmsResponse;

/**
 * 回调接口
 * 本质就是Consumer型，因此只要是能接收Consumer型的方法都可以使用该接口代替！
 * 因为本质就是传个lambda表达式，与这个接口名无关！！！
 *
 * @author zoutongkun
 */
@FunctionalInterface
public interface CallBack {
    /**
     * 回调逻辑，入参是前面的执行结果
     *
     * @param smsResponse
     */
    void callBack(SmsResponse smsResponse);
}
