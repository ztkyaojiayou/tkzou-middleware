package com.tkzou.middleware.dynamicdb.constant;

/**
 * 数据库类型常量
 * 在此之前需要先区分是哪个酒店，每个酒店都对应一套数据库！
 * 包括：生产/测试环境下的主/备库，共4个数据库
 *
 * @Description:
 * @Author: zoutongkun
 * @CreateDate: 2024/5/16 14:48
 */
public class DbTypeConstant {
    public static final String MYSQL_MASTER = "mysql_master";
    public static final String MYSQL_SLAVE = "mysql_slave";
    public static final String ORACLE_MASTER = "oracle_master";
    public static final String ORACLE_SLAVE = "oracle_slave";
}
