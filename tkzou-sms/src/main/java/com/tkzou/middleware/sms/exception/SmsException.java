package com.tkzou.middleware.sms.exception;

/**
 * @author zoutongkun
 */
public class SmsException extends RuntimeException {
    public String code;
    public String message;
    public String requestId;

    public SmsException(String message) {
        super(message);
        this.message = message;
    }

    public SmsException(String code, String message) {
        super("[" + code + "] " + message);
        this.message = message;
        this.code = code;
    }

    public SmsException(String code, String message, String requestId) {
        super("[" + code + "] " + message);
        this.message = message;
        this.code = code;
        this.requestId = requestId;
    }
}
