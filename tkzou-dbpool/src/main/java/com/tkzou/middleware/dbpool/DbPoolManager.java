package com.tkzou.middleware.dbpool;

import com.tkzou.middleware.dbpool.config.DbProperties;
import com.tkzou.middleware.dbpool.core.DbPoolService;
import com.tkzou.middleware.dbpool.core.DbPoolServiceImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.ResourceBundle;


/**
 * 数据库连接池管理
 * 核心类，也是用户开箱即用的类
 *
 * @author zoutongkun
 */
public class DbPoolManager {
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String STRING = "java.lang.String";
    /**
     * 类路径下的配置文件名称
     */
    private static String sourcePath = "database";

    /**
     * 数据库连接池配置属性
     */
    private static DbProperties properties = null;

    /**
     * 数据库连接池接口
     */
    private static DbPoolService connectionPool = null;

    /**
     * 初始化连接池
     *
     */
    static {
        try {
            synchronized (DbPoolManager.class) {
                if (properties == null) {
                    parseAndInitProps();
                    connectionPool = DbPoolServiceImpl.create(properties);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库连接池database配置文件解析并初始化
     * 若为spring，则非常方便，使用@Value即可
     *
     * @Description:
     */
    private static void parseAndInitProps() throws NoSuchFieldException, IllegalAccessException {
        properties = new DbProperties();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(sourcePath);
        //获取资源文件中所有的key
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            //反射获取类中的属性字段
            Field field = DbProperties.class.getDeclaredField(key);
            //属性字段的类型
            Type genericType = field.getGenericType();
            //属性设置可访问
            field.setAccessible(true);
            //根据key读取对应的value值
            String value = resourceBundle.getString(key);
            if (INT.equals(genericType.getTypeName())) {
                //反射给属性赋值
                field.set(properties, Integer.parseInt(value));
            } else if (LONG.equals(genericType.getTypeName())) {
                field.set(properties, Long.parseLong(value));
            } else if (STRING.equals(genericType.getTypeName())) {
                field.set(properties, value);
            }
        }
    }

    /**
     * @Description:获取连接
     */
    public static Connection getConnection() {
        return connectionPool.getConnection();
    }

    /**
     * @Description:释放连接
     */
    public static void release(Connection connection) {
        connectionPool.release(connection);
    }
}