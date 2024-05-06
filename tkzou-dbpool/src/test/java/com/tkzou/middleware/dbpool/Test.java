package com.tkzou.middleware.dbpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 测试类
 */
public class Test {
    public static void main(String[] args) {
        ThreadConnection threadConnection = new ThreadConnection();
        //20个客户端获取连接，每个客户端都获取20个连接并执行sql
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(threadConnection, "线程:" + i);
            thread.start();
        }
    }
}

/**
 * 创建20个连接
 */
class ThreadConnection implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            Connection connection = DbPoolManager.getConnection();
            System.out.println(Thread.currentThread().getName() + ",connection:" + connection);
            Statement statement;
            try {
                statement = connection.createStatement();
                String selectsql = "select * from test";
                statement.execute(selectsql);

                String insertsql = "insert into test(name) VALUES('" + Thread.currentThread().getName() + connection + "')";
                statement.execute(insertsql);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DbPoolManager.release(connection);
        }
    }
}
