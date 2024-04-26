package com.tkzou.middleware.binlog.core.exception;

/**
 * 统一异常定义
 *
 * @author zoutongkun
 */
public class BinlogException extends RuntimeException {
    /**
     * 异常信息只能通过构造器传入，因为父类并未提供set方法！！！
     * 且父类的message字段是private的，无法被当前子类直接使用！
     *
     * @param message
     */
    public BinlogException(String message) {
        super(message);
    }

    public static void main(String[] args) {
        BinlogException test = new BinlogException("test");
        System.out.println(test.getMessage());
    }
}
